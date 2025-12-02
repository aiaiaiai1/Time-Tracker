package org.project.timetracker.community;

import lombok.RequiredArgsConstructor;
import org.project.timetracker.auth.User;
import org.project.timetracker.auth.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommunityService {

    private final UserRepository userRepository;

    @Transactional
    public void addGoal(Long userId, String goal) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저 입니다."));
        user.setGoal(goal);
    }
}
