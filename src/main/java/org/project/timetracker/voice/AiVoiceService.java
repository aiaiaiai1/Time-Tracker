package org.project.timetracker.voice;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.vertexai.gemini.VertexAiGeminiChatModel;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;


@Service
@RequiredArgsConstructor
public class AiVoiceService {
    private final VertexAiGeminiChatModel chatModel;


    public String extractInfo(String memo) {
        if (memo == null || memo.isBlank()) {
            throw new IllegalArgumentException("비어있는 문자열입니다.");
        }

        String prompt = createPrompt(memo);

        String info = chatModel.call(prompt);
        return info;
    }


    private String createPrompt(String memo) {
        LocalDateTime now = LocalDateTime.now();
        return String.format("""
                        너의 역할은 사용자의 활동 기록(메모)을 보고, 활동기록을 요약하고 활동 기록의 날짜, 시작시간, 종료시간을 알아내는거야
                      
                        
                        [활동 메모]
                        %s            
                        
                        [지시]
                        - 활동 기록을 바탕으로 해당 날짜, 시작 시간, 종료 시간을 알아내줘.
                        - 활동 기록을 요약해주는데 문장이 아닌 구 형식으로 요약해줘. 
                        - 현재 시각은 %s 이고
                        시간 기준은 UTC+9, 24시간 format으로 하고 활동기록에서 따로 날짜가 특정되지 않으면 오늘 날짜를 기준으로 해줘.
                        - 응답은 차례대로 날짜, 시작 시간, 종료 시간 이고 ,로 구분해줘. 응답 예시 참고하면 될꺼야
                       
                        
                        [응답 예시]
                        20251008,13:00,15:30,축구
                        """,
                memo,
                now);
    }

}
