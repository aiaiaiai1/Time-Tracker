package org.project.timetracker.chatgpt;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.vertexai.gemini.VertexAiGeminiChatModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api")
@RestController
@RequiredArgsConstructor
public class ChatController {

    private final VertexAiGeminiChatModel vertexAiGeminiChatModel;


    @GetMapping("/chat")
    public ResponseEntity<String> chat(@RequestParam("message") String message) {

        String vertexAiGeminiResponse = vertexAiGeminiChatModel.call(message);
        return ResponseEntity.ok(vertexAiGeminiResponse);


    }
}
