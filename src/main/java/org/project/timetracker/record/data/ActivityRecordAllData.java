package org.project.timetracker.record.data;

import java.util.List;

public record ActivityRecordAllData(
        List<ActivityRecordMonthData> months
) {
}
