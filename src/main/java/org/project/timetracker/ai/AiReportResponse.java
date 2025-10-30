package org.project.timetracker.ai;

public record AiReportResponse(
        String report
) {
    public static AiReportResponse of(String report) {
        return new AiReportResponse(report);
    }
}
