package org.project.timetracker.record.data;

import java.time.format.DateTimeFormatter;
import org.project.timetracker.record.ActivityRecord;

public record ActivityRecordEntry(
        String start,
        String end,
        String category,
        String label
) {
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    public static ActivityRecordEntry from(ActivityRecord record) {
        return new ActivityRecordEntry(
                record.getStartTime().toLocalTime().format(TIME_FORMATTER),
                record.getEndTime().toLocalTime().format(TIME_FORMATTER),
                record.getCategory(),
                record.getMemo()
        );
    }

}
