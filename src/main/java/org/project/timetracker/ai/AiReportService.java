package org.project.timetracker.ai;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
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

    private ReportDateRages calculateLastWeekRanges() {
        LocalDate today = LocalDate.now();

        LocalDate lastWeekStart = today.minusWeeks(1)
                .with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate lastWeekEnd = today.minusWeeks(1)
                .with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));

        LocalDate prevWeekStart = today.minusWeeks(2)
                .with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate prevWeekEnd = today.minusWeeks(2)
                .with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));

        return new ReportDateRages(
                lastWeekStart.atStartOfDay(),
                lastWeekEnd.atTime(LocalTime.MAX),
                prevWeekStart.atStartOfDay(),
                prevWeekEnd.atTime(LocalTime.MAX)
        );
    }

    private ReportSummaries summarizeReportData(ReportData data) {
        String analysisSummary = processRecords(data.analysisRecords());
        String comparisonSummary = processRecords(data.comparisonRecords());
        String timeSlotSummary = processTimeSlotRecords(data.analysisRecords());

        return new ReportSummaries(analysisSummary, comparisonSummary, timeSlotSummary);
    }


    private String createPrompt(ReportSummaries summaries) {

        return String.format("""
                너는 전문 시간 관리 코치야.
                아래 [데이터]를 보고 사용자를 위한 주간 리포트를 작성해 줘.
                
                [지난주 요약 데이터]
                %s
                
                [지지난주 요약 데이터]
                %s
                
                [지난주 시간대별 데이터]
                %s
                
                [리포트 작성 가이드]
                1. [지난주]와 [지지난주] 데이터를 비교 분석해 줘. (예: "공부 시간이 2시간 증가했습니다.")
                2. [지난주 시간대별 데이터]를 바탕으로 사용자의 핵심 '활동 패턴'을 1~2문장으로 분석해 줘. (예: "주로 밤 시간대에 '공부' 활동이 집중되어 있습니다.")
                3. 위 내용을 종합해서 개선점이나 칭찬을 1~2문장으로 제안해 줘.
                """,
                summaries.analysisSummary(),
                summaries.comparisonSummary(),
                summaries.timeSlotSummary()
        );
    }


    private String processRecords(List<ActivityRecord> records) {
        if (records.isEmpty()) {
            return "기록된 데이터가 없습니다.";
        }
        // (임시 텍스트)
        return String.format("[총 %d건의 기록을 요약할 예정]", records.size());
    }

    private String processTimeSlotRecords(List<ActivityRecord> records) {
        if (records.isEmpty()) {
            return "기록된 데이터가 없습니다.";
        }

        return String.format("[총 %d건의 기록을 시간대별로 요약할 예정]", records.size());
    }
}
