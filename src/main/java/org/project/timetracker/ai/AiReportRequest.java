package org.project.timetracker.ai;

public record AiReportRequest(
        String token,
        String period
) {
    public static AiReportRequest of(String token, String period) {
        return new AiReportRequest(token, period);
    }
}
