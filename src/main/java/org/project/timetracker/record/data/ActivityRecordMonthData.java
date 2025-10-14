package org.project.timetracker.record.data;

import java.util.List;

public record ActivityRecordMonthData(
        String month,
        List<ActivityRecordDayData> days
) {

}
