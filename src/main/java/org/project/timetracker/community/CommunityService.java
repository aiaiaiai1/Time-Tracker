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

    private static final List<String> CATEGORY_OPTIONS = List.of(
            "공부", "건강", "취미", "커리어", "기타"
    );


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

    @Transactional
    public void addClassifiedGoal(Long userId, String goal) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저 입니다."));
        user.setGoal(goal);
        String classifiedGoal = chatModel.call(createPromptForClassifiedGoal(goal));
        user.setClassifiedGoal(classifiedGoal);
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
                        너의 역할은 사용자가 어떤 목표를 가지고 사용하는지를 입력으로 받아서
                        해당 목표를 적절하게 카테고리화 시키는거야.
                        
                        [사용자의 목표]
                        %s
                        
                        [존재하는 카테고리 목록]
                        %s
                        
                        [지시]
                        - 사용자가 원하는 목표를 존재하는 카테고리 목록을 보고 적절하게 카테고리화 해줘.                    
                        - 만약 너가 생각하기에 사용자의 목표에 따른 적절한 카테고리가 없다면 새로운 카테고리로 정의해서 만들어줘.
                        - 카테고리를 정의할떄는 광범위한 범위 보다는 자세하고 디테일한 범위인 카테고리로 설정해줘
                        - 응답 예시 참고해서 오직 하나의 단어나 구 형태로 응답해줘, 앞에 prefix는 없어도돼
                        
                        [응답 예시]
                        다이어트
                        """,
                goal,
                categoriesText);
    }

    public String createPromptForClassifiedGoal(String goal) {
        String categoriesText = String.join(", ", CATEGORY_OPTIONS);

        return String.format("""
                너의 역할은 사용자가 입력한 목표를 보고, 가장 적절한 카테고리 1개만 추천해서 분류하는 거야.
                
                [목표]
                %s

                [카테고리 선택지]
                %s

                [지시]
                - 목표를 확인하고 카테고리 선택지 중에서 가장 적합한 단어 1개만 골라서 다른 설명 없이 단어만 응답해 줘.

                [응답 예시]
                자기계발
                """,
                goal,
                categoriesText);
    }


    public List<UserData> findSimilarUsers(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저 입니다."));

        List<User> users = userRepository.findAllByGoalCategoryId(user.getGoalCategoryId());
        return users.stream()
                .map(u -> new UserData(u.getId(), u.getUsername()))
                .toList();

    }

    public String test(String goal){
        return chatModel.call(createPromptForClassifiedGoal(goal));
    }
}
