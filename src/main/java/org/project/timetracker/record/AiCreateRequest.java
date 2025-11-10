package org.project.timetracker.record;

public record AiCreateRequest(
        String token,
        String date,
        String startTime,
        String endTime,
        String memo
) {
}
