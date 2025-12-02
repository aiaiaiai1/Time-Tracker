package org.project.timetracker.community;

import lombok.RequiredArgsConstructor;
import org.project.timetracker.auth.User;
import org.project.timetracker.auth.UserRepository;
import org.springframework.ai.vertexai.gemini.VertexAiGeminiChatModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommunityService {

    private final UserRepository userRepository;
    private final GoalCategoryRepository goalCategoryRepository;
    private final VertexAiGeminiChatModel chatModel;

    @Transactional
    public void addGoal(Long userId, String goal) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저 입니다."));
        user.setGoal(goal);
        String goalTitle = pollGoalTitle(goal);
        GoalCategory goalCategory = saveGoalCategoryIfAbsent(goalTitle);
        user.setGoalCategoryId(goalCategory.getId());

    }

    private GoalCategory saveGoalCategoryIfAbsent(String goalTitle) {
        return goalCategoryRepository.findByTitle(goalTitle)
                .orElseGet(() -> goalCategoryRepository.save(new GoalCategory(goalTitle)));
    }


    private String pollGoalTitle(String goal) {
        List<GoalCategory> goalCategories = goalCategoryRepository.findAll();
        return chatModel.call(createPrompt(goal, goalCategories));
    }

    private String createPrompt(String goal, List<GoalCategory> goalCategories) {
        String categoriesText = goalCategories.stream()
                .map(GoalCategory::getTitle)
                .collect(Collectors.joining(", "));

        return String.format("""
                        너의 역할은 시간 가계부 어플을 사용하는 사용자가 있는데 사용자가 어떤 목적, 목표를 가지고 사용하는지를 입력으로 받아서
                        해당 목적, 목표를 적절하게 카테고리화 시키는거야.
                        
                        [사용자가 시간 가계부 어플을 사용하는 목적, 목표]
                        %s
                        
                        [존재하는 카테고리 목록]
                        %s
                        
                        [지시]
                        - 사용자가 원하는 목적을 입력으로 받아서 존재하는 카테고리 목록을 보고 적절한 카테고리로 골라줘.                    
                        - 만약 너가 생각하기에 사용자의 목적에 따른 적절한 카테고리가 없다면 새로운 카테고리로 정의해서 만들어줘.
                        - 카테고리를 정의할떄는 광범위한 범위 보다는 자세하고 디테일한 범위인 카테고리로 설정해줘
                        - 응답 예시 참고해서 오직 하나의 단어나 구 형태로 응답해줘, 앞에 prefix는 없어도돼
                        
                        [응답 예시]
                        다이어트
                        """,
                goal,
                categoriesText);
    }
}
