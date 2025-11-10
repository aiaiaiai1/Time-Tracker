package org.project.timetracker.ai;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.project.timetracker.record.ActivityRecord;
import org.project.timetracker.record.ActivityRecordRepository;
import org.springframework.ai.vertexai.gemini.VertexAiGeminiChatModel;
import org.springframework.stereotype.Service;



@Service
@RequiredArgsConstructor
public class AiReportService {
    private final VertexAiGeminiChatModel chatModel;
    private final ActivityRecordRepository activityRecordRepository;

    private record ReportDateRanges(
            LocalDateTime analysisStart, LocalDateTime analysisEnd,
            LocalDateTime comparisonStart, LocalDateTime comparisonEnd
    ) {}

    private record ReportData(
            List<ActivityRecord> analysisRecords,
            List<ActivityRecord> comparisonRecords
    ) {}

    private record ReportDataText(
            String analysisRecordsText,
            String comparisonRecordsText
    ) {}

    public AiReportResponse generateReport(Long userId, int period, String purpose) {
        if (period <= 0) {
            throw new IllegalArgumentException("기간은 0보다 커야 합니다.");
        }

        return generateDynamicReport(userId, period, purpose);
    }

    private AiReportResponse generateDynamicReport(Long userId, int period, String purpose) {
        //날짜 계산
        ReportDateRanges dateRanges = calculateDateRanges(period);

        //데이터 조회
        ReportData data = fetchReportData(userId, dateRanges);

        if (data.analysisRecords().isEmpty()) {
            return AiReportResponse.fromMessage(
                    String.format("최근 %d일간 기록된 활동 데이터가 없습니다. AI 리포트를 생성할 수 없습니다.", period)
            );
        }

        //데이터 요약
        ReportDataText dataText = processReportData(data);
        //프롬프트 생성
        String prompt = createPrompt(dataText, purpose);
        //AI 호출
        String fullReport = chatModel.call(prompt);

        int startIndex = fullReport.indexOf("REPORT_START");

        if (startIndex == -1) {
            return AiReportResponse.fromMessage(fullReport);
        }

        String cleanReport = fullReport.substring(startIndex + "REPORT_START".length());

        String[] parts = cleanReport.split("---BREAK---");

        if (parts.length < 5) {
            return AiReportResponse.fromMessage(fullReport);
        }

        return new AiReportResponse(
                parts[0].trim(), // 1. 한줄요약
                parts[1].trim(), // 2. 총평
                parts[2].trim(), // 3. 비교
                parts[3].trim(), // 4. 패턴
                parts[4].trim() //5. 제안
        );
    }

    private ReportDateRanges calculateDateRanges(int period) {
        LocalDate today = LocalDate.now();

        LocalDate analysisEnd = today.minusDays(1);
        LocalDate analysisStart = analysisEnd.minusDays(period - 1);

        LocalDate comparisonEnd = analysisStart.minusDays(1);
        LocalDate comparisonStart = comparisonEnd.minusDays(period - 1);

        return new ReportDateRanges(
                analysisStart.atStartOfDay(),
                analysisEnd.atTime(LocalTime.MAX),
                comparisonStart.atStartOfDay(),
                comparisonEnd.atTime(LocalTime.MAX)
        );
    }

    private ReportData fetchReportData(Long userId, ReportDateRanges dateRanges) {
        List<ActivityRecord> analysisRecords = activityRecordRepository.findByUserIdAndBetweenTime(
                userId, dateRanges.analysisStart(), dateRanges.analysisEnd());

        List<ActivityRecord> comparisonRecords = activityRecordRepository.findByUserIdAndBetweenTime(
                userId, dateRanges.comparisonStart(), dateRanges.comparisonEnd());

        return new ReportData(analysisRecords, comparisonRecords);
    }

    private ReportDataText processReportData(ReportData data) {
        String analysisText = formatRecordsToString(data.analysisRecords);
        String comparisonText = formatRecordsToString(data.comparisonRecords);

        return new ReportDataText(analysisText, comparisonText);
    }

    private String formatRecordsToString(List<ActivityRecord> records) {
        if (records.isEmpty()) {
            return "기록된 데이터가 없습니다.";
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        return records.stream()
                .sorted(Comparator.comparing(ActivityRecord::getStartTime))
                .map(record -> String.format(
                        "일시: %s, 카테고리: %s, 시간(분): %d, 메모: %s",
                        record.getStartTime().format(formatter),
                        record.getCategory(),
                        record.getSpanMinutes(),
                        (record.getMemo() != null && !record.getMemo().isBlank() ? record.getMemo() : "내용 없음")
                ))
                .collect(Collectors.joining("\n"));
    }


    private String createPrompt(ReportDataText dataText, String purpose) {
        String userPurpose = (purpose != null && !purpose.isBlank()) ? purpose : "특별한 목표 없음";

        return String.format("""
                너는 전문 시간 관리 코치이자 사용자의 특정 목표 달성을 돕는 전문 컨설턴트야.
                아래 [사용자 목표], [데이터]를 보고 사용자를 위한 리포트를 작성해 줘.
                [데이터]는 사용자의 모든 실제 기록을 시간순으로 정렬한 목록이야.
                
                [사용자 목표]
                %s
                
                [최근 기간 활동 내역]
                %s
                
                [이전 기간 활동 내역]
                %s
    
                [리포트 작성 가이드]
                1. 전체 리포트 내용을 한 문장으로 강력하게 요약해 줘. (예: "공부 시간은 늘었지만, 핵심 활동이 부족합니다.")
                2. 총평을 2~3문장으로 작성해 줘.
                3. 최근 기간과 이전 기간 활동 내역을 바탕으로 총 시간, 카테고리별 변화 등을 비교 분석해줘.
                4. 최근 기간 활동 내역의 일시를 보고 시간대별(오전/오후/밤/새벽) 활동 패턴을 분석해 줘.
                5. 실행 가능한 제안을 사용자 목표와 메모 내용을 바탕으로, 사용자가 당장 실행할 수 있는 구체적인 행동을 하나의 자연스러운 단락으로 이어서 제안해 줘.
                  - 왜 그 행동이 필요한지를 반드시 포함해야 해.
                  - (예: "취업 목표에 비해 코딩 테스트 연습이 전혀 없으시네요. 따라서 주 2회는 코딩 테스트 시간을 확보하시는 게 좋겠습니다. 또한 CS 이론 학습도 부족해 보이니...")

                [출력 형식]
                (아래 규칙을 반드시 엄격하게 지킬 것. 이 응답은 컴퓨터가 자동으로 파싱할 예정임.)
                
                1. 절대 다른 인사말, 부연 설명, 마크다운(**, ## 등)을 사용하지 마.
                2. 응답은 반드시 `REPORT_START`라는 단어로 시작해야 해.
                3. 5개의 섹션은 반드시 `---BREAK---` 구분자로 분리해야 해.
                4. 각 섹션에는 1., 2. 같은 번호나 [괄호] 같은 제목/레이블을 절대 넣지 마.
                5. 오직 순수 텍스트(plain text)로만 응답해.
                
                [응답 예시 (이 구조를 정확히 따를 것)]
                REPORT_START
                (여기에 1번 한 줄 요약 텍스트만 넣기)
                ---BREAK---
                (여기에 2번 총평 텍스트만 넣기)
                ---BREAK---
                (여기에 3번 비교 분석 텍스트만 넣기)
                ---BREAK---
                (여기에 4번 패턴 분석 텍스트만 넣기)
                ---BREAK---
                (여기에 5번 실행 가능한 제안 텍스트만 넣기)
                """,
                userPurpose,
                dataText.analysisRecordsText(),
                dataText.comparisonRecordsText()
        );
    }
}
