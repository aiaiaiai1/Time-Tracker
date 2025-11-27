package org.project.timetracker.record;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.project.timetracker.auth.TokenProcessor;
import org.project.timetracker.auth.User;
import org.project.timetracker.auth.UserRepository;
import org.project.timetracker.record.data.ActivityRecordAllData;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ActivityRecordService {

    private final ActivityRecordRepository activityRecordRepository;
    private final UserRepository userRepository;
    private final TokenProcessor tokenProcessor;
    private final ActivityRecordDtoMapper activityRecordDtoMapper;

    public ActivityRecordResponse create(ActivityRecordCreateRequest request) {
        Long userId = tokenProcessor.parseToken(request.token());
        User user = findUserById(userId);

        LocalDateTime startTime = parseDateTime(request.date(), request.startTime());
        LocalDateTime endTime = parseDateTime(request.date(), request.endTime());

        if (activityRecordRepository.existsOverlappingRecords(userId, startTime, endTime)) {
            throw new IllegalArgumentException("이미 해당 시간대에 일정이 존재합니다.");
        }

        RecordSource source = request.source() != null ? RecordSource.valueOf(request.source()) : null;

        ActivityRecord newRecord = ActivityRecord.create(
                user,
                startTime,
                endTime,
                request.category(),
                request.memo(),
                source);

        activityRecordRepository.save(newRecord);

        return buildAllDataResponse(userId, "전체 데이터 조회 성공");
    }

    public ActivityRecordResponse deleteSchedule(ActivityRecordDeleteRequest request) {
        Long userId = tokenProcessor.parseToken(request.token());
        LocalDateTime startTime = parseDateTime(request.date(), request.startTime());

        ActivityRecord recordToDelete = activityRecordRepository.findByUserIdAndStartTime(userId, startTime)
                .orElseThrow(() -> new IllegalArgumentException("해당 시간대 일정이 없습니다."));

        activityRecordRepository.delete(recordToDelete);

        return buildAllDataResponse(userId, "전체 데이터 조회 성공");
    }

    private ActivityRecordResponse buildAllDataResponse(Long userId, String message) {
        List<ActivityRecord> allRecords = activityRecordRepository.findByUserIdOrderByStartTimeAsc(userId);

        ActivityRecordAllData allData = activityRecordDtoMapper.mapToAllData(allRecords);

        return ActivityRecordResponse.success(message, allData);
    }


    private User findUserById(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
    }

    private LocalDateTime parseDateTime(String date, String time) {
        return LocalDateTime.of(LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyyMMdd")),
                LocalTime.parse(time, DateTimeFormatter.ofPattern("HH:mm")));
    }
}
