package org.project.timetracker.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
    private final TokenProcessor tokenProcessor;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        User user = userRepository.findByLoginId(loginRequest.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("유저가 존재하지 않습니다."));

        if (!user.getPassword().equals(loginRequest.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        String accessToken = tokenProcessor.generateAccessToken(user.getId());
        LoginResponse response = new LoginResponse(true, user.getLoginId(), "로그인 성공", accessToken);

        return ResponseEntity.ok().body(response);
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@RequestBody RegisterRequest registerRequest) {
        try {
            User user = new User(registerRequest.getEmail(), registerRequest.getPassword(), registerRequest.getPassword());
            userRepository.save(user);
            String accessToken = tokenProcessor.generateAccessToken(user.getId());
            RegisterResponse response = new RegisterResponse(true, "회원가입 성공", accessToken);
            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            throw new IllegalArgumentException("회원가입 실패", e);
        }
    }

    @PostMapping("/duplicate-check")
    public ResponseEntity<DuplicateCheckResponse> duplicateCheck(@RequestBody DuplicateCheckRequest request) {
        boolean isDuplicate = userRepository.existsByLoginId(request.email());

        if (isDuplicate) {
            return ResponseEntity.ok().body(DuplicateCheckResponse.ofFail());
        } else {
            return ResponseEntity.ok().body(DuplicateCheckResponse.ofSuccess());
        }
    }
}
