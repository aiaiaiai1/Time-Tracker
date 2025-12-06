package org.project.timetracker.community;

import lombok.RequiredArgsConstructor;
import org.project.timetracker.auth.TokenProcessor;
import org.project.timetracker.common.MessageOnlyResponse;
import org.project.timetracker.common.TokenOnlyRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class CommunityController {

    private final CommunityService communityService;
    private final TokenProcessor tokenProcessor;

    @PostMapping("/my")
    public ResponseEntity<MessageOnlyResponse> addUserGoal(@RequestBody AddGoalRequest request) {
        Long id = tokenProcessor.parseToken(request.token());
        communityService.addGoal(id, request.goal());
        return ResponseEntity.ok(new MessageOnlyResponse(true, "저장 성공"));
    }

    @PostMapping("/similar")
    public ResponseEntity<UserResponse> getUserList(@RequestBody TokenOnlyRequest request) {
        Long id = tokenProcessor.parseToken(request.token());
        List<UserData> userData = communityService.findSimilarUsers(id);
        return ResponseEntity.ok(new UserResponse(true, "저장 성공", userData));
    }


}
