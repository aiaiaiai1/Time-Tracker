package org.project.timetracker.record;

public record ActivityRecordDeleteRequest(
        String token,
        String date,
        String startTime
) {
}
