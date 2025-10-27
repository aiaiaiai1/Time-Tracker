package org.project.timetracker.record.data;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;

public record ActivityRecordMonthData(
        String month,
        List<ActivityRecordDayData> days
) {
    private static final DateTimeFormatter MONTH_FORMATTER = DateTimeFormatter.ofPattern("yyyyMM");

    public static ActivityRecordMonthData from(YearMonth yearMonth, List<ActivityRecordDayData> days) {
        return new ActivityRecordMonthData(
                yearMonth.format(MONTH_FORMATTER),
                days
        );
    }
}
