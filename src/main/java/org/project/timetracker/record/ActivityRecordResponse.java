package org.project.timetracker.record;


import com.fasterxml.jackson.annotation.JsonInclude;
import org.project.timetracker.record.data.ActivityRecordAllData;
import org.project.timetracker.record.data.ActivityRecordMonthData;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ActivityRecordResponse(
        boolean success,
        String message,
        ActivityRecordAllData data
) {
    public static ActivityRecordResponse success(String message, ActivityRecordAllData data) {
        return new ActivityRecordResponse(true, message, data);
    }
}
