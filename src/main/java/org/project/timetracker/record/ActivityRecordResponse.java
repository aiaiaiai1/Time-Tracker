package org.project.timetracker.record;


import com.fasterxml.jackson.annotation.JsonInclude;
import org.project.timetracker.record.data.ActivityRecordMonthData;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ActivityRecordResponse(
        boolean success,
        String message,
        ActivityRecordMonthData data
) {
    public static ActivityRecordResponse success(String message, ActivityRecordMonthData data) {
        return new ActivityRecordResponse(true, message, data);
    }
}
