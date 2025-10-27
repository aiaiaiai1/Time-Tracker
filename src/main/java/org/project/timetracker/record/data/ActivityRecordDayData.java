package org.project.timetracker.record.data;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;

public record ActivityRecordDayData(
        String date,
        int day,
        String weekday,
        List<ActivityRecordEntry> entries
) {
    public static ActivityRecordDayData from(LocalDate date, List<ActivityRecordEntry> entries) {
        return new ActivityRecordDayData(
                date.format(DateTimeFormatter.ISO_LOCAL_DATE),
                date.getDayOfMonth(),
                date.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.KOREAN),
                entries
        );
    }

}
