package org.project.timetracker.ai;

import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.project.timetracker.record.ActivityRecord;
import org.springframework.ai.vertexai.gemini.VertexAiGeminiChatModel;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AiReportService {
    private final VertexAiGeminiChatModel chatModel;

    private record ReportDateRages(
            LocalDateTime analysisStart, LocalDateTime analysisEnd,
            LocalDateTime comparisonStart, LocalDateTime comparisonEnd
    ) {}

    private record ReportData(
            List<ActivityRecord> analysisRecords,
            List<ActivityRecord> comparisonRecords
    ) {}

    private record ReportSummaries(
            String analysisSummary,
            String comparisonSummary,
            String timeSlotSummary
    ) {}

    public String generateReport(Long userId, String period) {
        if ("last_week".equals(period)) {
            return generateLastWeekReport(userId);
        }

        throw new IllegalArgumentException("잘못된 기간: " + period);
    }

    private String generateLastWeekReport(Long userId) {
        //날짜 계산

        //데이터 조회

        //데이터 요약

        //프롬프트 생성

        //AI 호출

        return "임시응답";
    }
}
