package org.project.timetracker.voice;

public record CreationResponse(
        String date,
        String startTime,
        String endTime,
        String memo,
        String category
) {
}
