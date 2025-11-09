package org.project.timetracker.ai;

public record AiReportRequest(
        String token,
        int period,
        String purpose
) {
    public static AiReportRequest of(String token, int period, String purpose) {
        return new AiReportRequest(token, period, purpose);
    }
}
