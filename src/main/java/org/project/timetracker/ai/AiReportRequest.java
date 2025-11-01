package org.project.timetracker.ai;

public record AiReportRequest(
        String token,
        int period
) {
    public static AiReportRequest of(String token, int period) {
        return new AiReportRequest(token, period);
    }
}
