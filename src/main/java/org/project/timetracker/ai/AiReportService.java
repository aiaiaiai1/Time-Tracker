package org.project.timetracker.ai;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.project.timetracker.record.ActivityRecord;
import org.project.timetracker.record.ActivityRecordRepository;
import org.project.timetracker.statistic.StatistcsData;
import org.project.timetracker.statistic.StatisticsCalculator;
import org.springframework.ai.vertexai.gemini.VertexAiGeminiChatModel;
import org.springframework.stereotype.Service;



@Service
@RequiredArgsConstructor
public class AiReportService {
    private final VertexAiGeminiChatModel chatModel;
    private final ActivityRecordRepository activityRecordRepository;
    private final StatisticsCalculator statisticsCalculator;

    private record ReportDateRanges(
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

    public AiReportResponse generateReport(Long userId, String period) {
        if ("last_week".equals(period)) {
            return generateLastWeekReport(userId);
        }

        throw new IllegalArgumentException("잘못된 기간: " + period);
    }

    private AiReportResponse generateLastWeekReport(Long userId) {
        //날짜 계산
        ReportDateRanges dateRanges = calculateLastWeekRanges();

        //데이터 조회
        ReportData data = fetchReportData(userId, dateRanges);

        if (data.analysisRecords().isEmpty()) {
            return AiReportResponse.fromMessage("지난주에 기록된 활동 데이터가 없습니다. AI 리포트를 생성할 수 없습니다.");
        }

        //데이터 요약
        ReportSummaries summaries = summarizeReportData(data);
        //프롬프트 생성
        String prompt = createPrompt(summaries);
        //AI 호출
        String fullReport = chatModel.call(prompt);

        int startIndex = fullReport.indexOf("REPORT_START");

        if (startIndex == -1) {
            return AiReportResponse.fromMessage(fullReport);
        }

        String cleanReport = fullReport.substring(startIndex + "REPORT_START".length());

        String[] parts = cleanReport.split("---BREAK---");

        if (parts.length < 4) {
            return AiReportResponse.fromMessage(fullReport);
        }

        return new AiReportResponse(
                parts[0].trim(), // 1. 총평
                parts[1].trim(), // 2. 비교
                parts[2].trim(), // 3. 패턴
                parts[3].trim()  // 4. 제안
        );
    }

    private ReportDateRanges calculateLastWeekRanges() {
        LocalDate today = LocalDate.now();

        LocalDate lastWeekStart = today.minusWeeks(1)
                .with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate lastWeekEnd = today.minusWeeks(1)
                .with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));

        LocalDate prevWeekStart = today.minusWeeks(2)
                .with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate prevWeekEnd = today.minusWeeks(2)
                .with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));

        return new ReportDateRanges(
                lastWeekStart.atStartOfDay(),
                lastWeekEnd.atTime(LocalTime.MAX),
                prevWeekStart.atStartOfDay(),
                prevWeekEnd.atTime(LocalTime.MAX)
        );
    }

    private ReportData fetchReportData(Long userId, ReportDateRanges dateRanges) {
        List<ActivityRecord> analysisRecords = activityRecordRepository.findByUserIdAndBetweenTime(
                userId, dateRanges.analysisStart(), dateRanges.analysisEnd());

        List<ActivityRecord> comparisonRecords = activityRecordRepository.findByUserIdAndBetweenTime(
                userId, dateRanges.comparisonStart(), dateRanges.comparisonEnd());

        return new ReportData(analysisRecords, comparisonRecords);
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
                1. [총평]을 1~2문장으로 요약해 줘.
                2. [지난주]와 [지지난주] 데이터를 비교 분석해 줘.
                3. [지난주 시간대별 데이터]를 바탕으로 사용자의 핵심 '활동 패턴'을 분석해 줘.
                4. 위 내용을 종합해서 개선점이나 칭찬을 제안해 줘.

                [출력 형식]
                - 다른 인사말이나 부연 설명은 일절 하지 마.
                - 반드시 `REPORT_START`로 응답을 시작해 줘.
                - 각 섹션 사이에 `---BREAK---` 구분자를 정확히 넣어줘.
                - 절대 마크다운(**, ## 등)을 사용하지 말고, 순수 텍스트(plain text)로만 응답해 줘.

                REPORT_START
                (1. 총평 텍스트)
                ---BREAK---
                (2. 비교 분석 텍스트)
                ---BREAK---
                (3. 패턴 분석 텍스트)
                ---BREAK---
                (4. 제안 텍스트)
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

        Map<String, StatistcsData> miniResults = statisticsCalculator.getStatisticsByCategory(records);

        long totalAmount = miniResults.values().stream()
                .mapToLong(StatistcsData::getAmount)
                .sum();

        if (totalAmount == 0) {
            return "기록된 시간이 없습니다.";
        }

        StringBuilder summaryText = new StringBuilder();

        summaryText.append(String.format("총 기록 시간: %d시간 %d분\n", totalAmount / 60, totalAmount % 60));

        for (Map.Entry<String, StatistcsData> entry : miniResults.entrySet()) {
            String category = entry.getKey();
            long amount = entry.getValue().getAmount();

            long hours = amount / 60;
            long minutes = amount % 60;
            long percentage = Math.round((double) amount / totalAmount * 100);

            summaryText.append(String.format(
                    "- %s: %d시간 %d분 (약 %d%%)\n", // 가독성을 위해 형식 수정
                    category, hours, minutes, percentage
            ));
        }

        return summaryText.toString();
    }

    private String processTimeSlotRecords(List<ActivityRecord> records) {
        if (records.isEmpty()) {
            return "기록된 데이터가 없습니다.";
        }

        Map<String, List<ActivityRecord>> timeSlotGroups = records.stream()
                .collect(Collectors.groupingBy(record -> getTimeSlot(record.getStartTime().toLocalTime())));

        StringBuilder summaryText = new StringBuilder();

        timeSlotGroups.entrySet().stream()
                .sorted(Map.Entry.comparingByKey(Comparator.comparing(this::getTimeSlotOrder)))
                .forEach(entry -> {
                    String timeSlot = entry.getKey();
                    List<ActivityRecord> timeSlotRecords = entry.getValue();

                    Map<String, Long> categoryAmounts = timeSlotRecords.stream()
                            .collect(Collectors.groupingBy(
                                    ActivityRecord::getCategory,
                                    Collectors.summingLong(ActivityRecord::getSpanMinutes)
                            ));

                    summaryText.append(String.format("\n[%s]\n", timeSlot));

                    categoryAmounts.entrySet().stream()
                            .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                            .forEach(categoryEntry -> {
                                String category = categoryEntry.getKey();
                                long amount = categoryEntry.getValue();
                                if (amount > 0) {
                                    summaryText.append(String.format(
                                            "- %s: %d시간 %d분\n",
                                            category, amount / 60, amount % 60
                                    ));
                                }
                            });
                });

        return summaryText.toString();
    }

    private String getTimeSlot(LocalTime time) {
        int hour = time.getHour();

        if (hour >= 0 && hour < 6) {
            return "새벽 (00시~06시)";
        } else if (hour >= 6 && hour < 12) {
            return "오전 (06시~12시)";
        } else if (hour >= 12 && hour < 18) {
            return "오후 (12시~18시)";
        } else {
            return "밤 (18시~24시)";
        }
    }

    private int getTimeSlotOrder(String timeSlot) {
        if (timeSlot.startsWith("새벽")) return 1;
        if (timeSlot.startsWith("오전")) return 2;
        if (timeSlot.startsWith("오후")) return 3;
        if (timeSlot.startsWith("밤")) return 4;
        return 5;
    }
}
