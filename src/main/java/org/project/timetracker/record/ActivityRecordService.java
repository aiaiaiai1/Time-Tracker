package org.project.timetracker.record;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.project.timetracker.auth.TokenProcessor;
import org.project.timetracker.auth.User;
import org.project.timetracker.auth.UserRepository;
import org.project.timetracker.record.data.ActivityRecordDayData;
import org.project.timetracker.record.data.ActivityRecordEntry;
import org.project.timetracker.record.data.ActivityRecordMonthData;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ActivityRecordService {

    private final ActivityRecordRepository activityRecordRepository;
    private final UserRepository userRepository;
    private final TokenProcessor tokenProcessor;

    public ActivityRecordResponse create(ActivityRecordCreateRequest request) {
        Long userId = tokenProcessor.parseToken(request.token());
        User user = findUserById(userId);

        LocalDateTime startTime = parseDateTime(request.date(), request.startTime());
        LocalDateTime endTime = parseDateTime(request.date(), request.endTime());

        if (activityRecordRepository.existsOverlappingRecords(userId, startTime, endTime)) {
            throw new IllegalArgumentException("이미 해당 시간대에 일정이 존재합니다.");
        }

        ActivityRecord newRecord = ActivityRecord.create(
                user,
                startTime,
                endTime,
                request.category(),
                request.memo());

        activityRecordRepository.save(newRecord);

        return buildMonthlyResponse(userId, request.date().substring(0, 6), "일정이 성공적으로 추가되었습니다.");
    }

    public ActivityRecordResponse deleteSchedule(ActivityRecordDeleteRequest request) {
        Long userId = tokenProcessor.parseToken(request.token());
        LocalDateTime startTime = parseDateTime(request.date(), request.startTime());

        ActivityRecord recordToDelete = activityRecordRepository.findByUserIdAndStartTime(userId, startTime)
                .orElseThrow(() -> new IllegalArgumentException("해당 시간대 일정이 없습니다."));

        String yearMonth = recordToDelete.getStartTime().format(DateTimeFormatter.ofPattern("yyyyMM"));
        activityRecordRepository.delete(recordToDelete);

        return buildMonthlyResponse(userId, yearMonth, "ok");
    }

    private ActivityRecordResponse buildMonthlyResponse(Long userId, String yearMonthStr, String message) {
        YearMonth yearMonth = YearMonth.parse(yearMonthStr, DateTimeFormatter.ofPattern("yyyyMM"));
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();

        List<ActivityRecord> records = activityRecordRepository.findByUserIdAndStartTimeBetweenOrderByStartTimeAsc(userId, startDate.atStartOfDay(), endDate.plusDays(1).atStartOfDay());
        Map<LocalDate, List<ActivityRecord>> recordsByDate = records.stream()
                .collect(Collectors.groupingBy(
                        r -> r.getStartTime().toLocalDate()));

        List<ActivityRecordDayData> days = recordsByDate.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> {
                    LocalDate date = entry.getKey();
                    List<ActivityRecord> dailyRecords = entry.getValue();

                    List<ActivityRecordEntry> entries = dailyRecords.stream()
                            .map(r -> new ActivityRecordEntry(
                                    r.getStartTime().toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm")),
                                    r.getEndTime().toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm")),
                                    r.getMemo()
                            )).collect(Collectors.toList());

                    return new ActivityRecordDayData(
                            date.format(DateTimeFormatter.ISO_LOCAL_DATE),
                            date.getDayOfMonth(),
                            date.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.KOREAN),
                            entries
                    );
                }).collect(Collectors.toList());

        ActivityRecordMonthData monthData = new ActivityRecordMonthData(yearMonthStr, days);
        return ActivityRecordResponse.success(message, monthData);
    }

    private User findUserById(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
    }

    private LocalDateTime parseDateTime(String date, String time) {
        return LocalDateTime.of(LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyyMMdd")),
                LocalTime.parse(time, DateTimeFormatter.ofPattern("HH:mm")));
    }
}
