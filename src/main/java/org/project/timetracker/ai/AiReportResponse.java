package org.project.timetracker.ai;

public record AiReportResponse(
        String oneLineSummary,
        String totalSummary,
        String comparisonReport,
        String patternReport,
        String suggestionReport
) {

    public static AiReportResponse fromMessage(String message) {
        return new AiReportResponse(message, "", "", "", "");
    }
}
