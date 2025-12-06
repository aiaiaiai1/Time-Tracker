package org.project.timetracker.record;

public record ActivityRecordCreateRequest(
        String token,
        String date,
        String startTime,
        String endTime,
        String category,
        String memo,
        String source
) {
    public static ActivityRecordCreateRequest fromAiRequest(
            AiCreateRequest aiRequest,
            String category
    ) {
        return new ActivityRecordCreateRequest(
                aiRequest.token(),
                aiRequest.date(),
                aiRequest.startTime(),
                aiRequest.endTime(),
                category,
                aiRequest.memo(),
                aiRequest.source()
        );
    }
}
