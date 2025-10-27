package org.project.timetracker.record;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.project.timetracker.record.data.ActivityRecordAllData;
import org.project.timetracker.record.data.ActivityRecordDayData;
import org.project.timetracker.record.data.ActivityRecordEntry;
import org.project.timetracker.record.data.ActivityRecordMonthData;
import org.springframework.stereotype.Component;

@Component
public class ActivityRecordDtoMapper {
    //ActivityRecordAllData 반환 메서드(최종)
    public ActivityRecordAllData mapToAllData(List<ActivityRecord> allRecords) {
        Map<YearMonth, Map<LocalDate, List<ActivityRecord>>> recordsByYearMonthAndDay = groupRecordsByYearMonthAndDay(allRecords);
        List<ActivityRecordMonthData> monthDataList = mapToMonthDataList(recordsByYearMonthAndDay);
        return new ActivityRecordAllData(monthDataList);
    }

    private Map<YearMonth, Map<LocalDate, List<ActivityRecord>>> groupRecordsByYearMonthAndDay(List<ActivityRecord> allRecords) {
        return allRecords.stream()
                .collect(Collectors.groupingBy(
                        record -> YearMonth.from(record.getStartTime()),
                        LinkedHashMap::new,
                        Collectors.groupingBy(
                                record -> record.getStartTime().toLocalDate(),
                                LinkedHashMap::new,
                                Collectors.toList()
                        )
                ));
    }

    //YearMonth - LocalDate, List<ActivityRecord> --> List<ActivityRecordMonthData> 변환 메서드
    private List<ActivityRecordMonthData> mapToMonthDataList(Map<YearMonth, Map<LocalDate, List<ActivityRecord>>> monthMap) {
        return monthMap.entrySet().stream()
                .map(monthEntry -> {
                    YearMonth yearMonth = monthEntry.getKey();
                    Map<LocalDate, List<ActivityRecord>> dailyMap = monthEntry.getValue();
                    List<ActivityRecordDayData> dayDataList = mapToDayDataList(dailyMap);

                    return ActivityRecordMonthData.from(yearMonth, dayDataList);
                })
                .toList();
    }

    //LocalDate, List<ActivityRecord> --> List<ActivityRecordDayData> 변환 메서드
    private List<ActivityRecordDayData> mapToDayDataList(Map<LocalDate, List<ActivityRecord>> dailyMap) {
        return dailyMap.entrySet().stream()
                .map(dayEntry -> {
                    LocalDate date = dayEntry.getKey();
                    List<ActivityRecord> dailyRecords = dayEntry.getValue();
                    List<ActivityRecordEntry> entries = mapToEntryList(dailyRecords);

                    return ActivityRecordDayData.from(date, entries);
                })
                .toList();
    }

    //List<ActivityRecord> --> List<ActivityRecordEntry> 변환 메서드
    private List<ActivityRecordEntry> mapToEntryList(List<ActivityRecord> activityRecords) {
        return activityRecords.stream()
                .map(ActivityRecordEntry::from)
                .toList();
    }
}
