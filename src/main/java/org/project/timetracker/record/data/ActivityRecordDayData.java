package org.project.timetracker.record.data;

import java.util.List;

public record ActivityRecordDayData(
        String date,
        int day,
        String weekday,
        List<ActivityRecordEntry> entries
) {

}
