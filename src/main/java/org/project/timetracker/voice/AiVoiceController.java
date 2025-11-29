package org.project.timetracker.voice;

import lombok.RequiredArgsConstructor;
import org.project.timetracker.ai.AiCategoryService;
import org.project.timetracker.auth.TokenProcessor;
import org.project.timetracker.record.ActivityRecordCreateRequest;
import org.project.timetracker.record.ActivityRecordResponse;
import org.project.timetracker.record.ActivityRecordService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class AiVoiceController {
    private final TokenProcessor tokenProcessor;
    private final AiVoiceService aiVoiceService;
    private final AiCategoryService aiCategoryService;
    private final ActivityRecordService activityRecordService;

    @PostMapping("/voices")
    public ResponseEntity<CreationResponse> createByVoice(@RequestBody CreationByVoiceRequest request) {
        Long userId = tokenProcessor.parseToken(request.token());
        String voice = request.voice();
        String category = aiCategoryService.recommendCategory(voice);
        String[] info = aiVoiceService.extractInfo(voice).split(",");

        return ResponseEntity.ok(new CreationResponse(
                info[0],
                info[1],
                info[2],
                info[3],
                category));
    }

    @PostMapping("/voices-v2")
    public ResponseEntity<ActivityRecordResponse> createByVoiceWithoutCheck(@RequestBody CreationByVoiceRequest request) {
        Long userId = tokenProcessor.parseToken(request.token());
        String voice = request.voice();
        String category = aiCategoryService.recommendCategory(voice);
        String[] info = aiVoiceService.extractInfo(voice).split(",");

        ActivityRecordCreateRequest newRequest = new ActivityRecordCreateRequest(
                request.token(),
                info[0],
                info[1],
                info[2],
                category,
                info[3],
                "USER"
        );

        ActivityRecordResponse response = activityRecordService.create(newRequest);
        return ResponseEntity.ok(response);
    }

}
