package org.project.timetracker.record;

public record ActivityRecordCreateRequest(
        String token,
        String date,
        String startTime,
        String endTime,
        String category,
        String memo
) {
}
