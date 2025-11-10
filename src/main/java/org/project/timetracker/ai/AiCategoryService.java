package org.project.timetracker.ai;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.vertexai.gemini.VertexAiGeminiChatModel;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AiCategoryService {
    private final VertexAiGeminiChatModel chatModel;

    private static final List<String> CATEGORY_OPTIONS = List.of(
            "생활", "일", "자기계발", "사교", "여가", "정신",  "기타"
    );

    private String createPrompt(String memo) {
        String categoriesText = String.join(", ", CATEGORY_OPTIONS);

        return String.format("""
                너의 역할은 사용자의 활동 기록(메모)을 보고, 가장 적절한 카테고리 1개만 추천해서 분류하는 거야.
                
                [활동 메모]
                %s

                [카테고리 선택지]
                %s

                [지시]
                - 활동 메모의 맥락을 분석해 줘.
                - 카테고리 선택지 중에서 가장 적합한 단어 1개만 골라서 다른 설명 없이 단어만 응답해 줘.

                [응답 예시]
                자기계발
                """,
                memo,
                categoriesText);
    }
}
