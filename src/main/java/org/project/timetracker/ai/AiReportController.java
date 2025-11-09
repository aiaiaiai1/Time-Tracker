package org.project.timetracker.ai;

import lombok.RequiredArgsConstructor;
import org.project.timetracker.auth.TokenProcessor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class AiReportController {
    private final TokenProcessor tokenProcessor;
    private final AiReportService aiReportService;

    @PostMapping("/ai")
    public ResponseEntity<AiReportResponse> getAiReport(@RequestBody AiReportRequest request) {
        Long userId = tokenProcessor.parseToken(request.token());
        int period = request.period();
        String purpose = request.purpose();

        AiReportResponse response = aiReportService.generateReport(userId, period, purpose);

        return ResponseEntity.ok(response);
    }
}
