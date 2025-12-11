package org.project.timetracker.statistic;

import lombok.RequiredArgsConstructor;
import org.project.timetracker.auth.TokenProcessor;
import org.project.timetracker.auth.User;
import org.project.timetracker.auth.UserRepository;
import org.project.timetracker.record.ActivityRecord;
import org.project.timetracker.record.ActivityRecordRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class StatisticsController {

    private final TokenProcessor tokenProcessor;
    private final UserRepository userRepository;
    private final ActivityRecordRepository activityRecordRepository;
    private final StatisticsCalculator statisticsCalculator;

    @PostMapping("/api/statistics")
    public ResponseEntity<StatisticsResponse> getStatisticsForComparing(@RequestBody StatisticsRequest request) {
        Long userId = tokenProcessor.parseToken(request.getToken());
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        LocalDate start = LocalDate.parse(request.getStartDate(), formatter);
        LocalDate end = LocalDate.parse(request.getEndDate(), formatter);

        LocalDateTime startDate = start.atStartOfDay();
        LocalDateTime endDate = end.atTime(23, 59, 59);

        long timeTotal = Duration.between(startDate, endDate).toMinutes();

        List<ActivityRecord> activityRecords = activityRecordRepository.findByUserIdAndBetweenTime(user.getId(), startDate, endDate);

        Map<String, StatistcsData> miniResults = statisticsCalculator.getStatisticsByCategory(activityRecords);

        Map<String, Map<String, StatisticsDetailData>> detailResults = statisticsCalculator.getStatisticsDetailsByName(activityRecords);

        long amountTotal = miniResults.values().stream()
                .mapToLong(StatistcsData::getAmount)
                .sum();

        List<StatisticsDataResponse> dataResponse = getTotalStatistics(miniResults, detailResults, amountTotal, timeTotal);

        return ResponseEntity.ok(new StatisticsResponse(dataResponse));

    }

    private List<StatisticsDataResponse> getTotalStatistics(
            Map<String, StatistcsData> miniResults,
            Map<String, Map<String, StatisticsDetailData>> detailResults,
            long amountTotal, long timeTotal
    ) {
        List<StatisticsDataResponse> dataResponse = new ArrayList<>();

        for (Map.Entry<String, StatistcsData> miniResult : miniResults.entrySet()) {
            String category = miniResult.getKey();
            StatistcsData activityRecordValue = miniResult.getValue();

            long amount = activityRecordValue.getAmount();

            String formattedAmount = getFormattedAmount(amount);

            long timePercent = Math.round((float) amount / amountTotal * 100);
            long accordPercent = Math.round((float) amount / timeTotal * 100);

            Map<String, StatisticsDetailData> detailMaps = detailResults.get(category);

            List<StatisticsDetailResponse> detailResponses = new ArrayList<>();

            for (Map.Entry<String, StatisticsDetailData> details : detailMaps.entrySet()) {
                String name = details.getKey();
                StatisticsDetailData value = details.getValue();
                String formattedDetailAmount = getFormattedAmount(value.getAmount());

                detailResponses.add(new StatisticsDetailResponse(name, formattedDetailAmount, value.getFrequency()));
            }

            dataResponse.add(new StatisticsDataResponse(
                    category, activityRecordValue.getFrequency(),
                    formattedAmount, timePercent, accordPercent, detailResponses)
            );
        }
        return dataResponse;
    }

    private String getFormattedAmount(long amount) {
        long hours = amount / 60;
        long minutes = amount % 60;

        return String.format("%02d:%02d", hours, minutes);
    }


    @PostMapping("/api/compare/v2")
    public ResponseEntity<ComparingResponse> getStatisticsForComparing(@RequestBody ComparingRequest request) {
        Long userId = tokenProcessor.parseToken(request.getToken());
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        List<ActivityRecord> activityRecords = activityRecordRepository.findAllByUserId(user.getId());
        Map<String, StatistcsData> statisticsByCategory = statisticsCalculator.getStatisticsByCategory(activityRecords);
        long userTotalMinutes = statisticsByCategory.values().stream()
                .mapToLong(StatistcsData::getAmount)
                .sum();
        List<CategoryData> myData = getCategoryData(statisticsByCategory, userTotalMinutes);

        List<UserTimeData> userTimeData = new ArrayList<>();

        List<User> users = userRepository.findAllByGoalCategoryId(user.getGoalCategoryId());
        users.remove(user);

        for (User target : users) {
            List<ActivityRecord> targetActivityRecords = activityRecordRepository.findAllByUserId(target.getId());
            Map<String, StatistcsData> targetStatisticsByCategory = statisticsCalculator.getStatisticsByCategory(targetActivityRecords);
            long targetTotalMinutes = targetStatisticsByCategory.values().stream()
                    .mapToLong(StatistcsData::getAmount)
                    .sum();
            List<CategoryData> targetCategoryData = getCategoryData(targetStatisticsByCategory, targetTotalMinutes);
            userTimeData.add(
                    new UserTimeData(target.getUsername(), (int) targetTotalMinutes, target.getGoal(), targetCategoryData)
            );
        }

        userTimeData.sort(Comparator.comparing(UserTimeData::getTotalMinutes).reversed());
        userTimeData.addFirst(new UserTimeData(user.getUsername(), (int) userTotalMinutes, user.getGoal(), myData));

        return ResponseEntity.ok(new ComparingResponse(true, userTimeData));
    }

    private List<CategoryData> getCategoryData(Map<String, StatistcsData> statisticsByCategory, long totalMinutes) {
        List<CategoryData> categoryData = new ArrayList<>();
        for (Map.Entry<String, StatistcsData> entry : statisticsByCategory.entrySet()) {
            String category = entry.getKey();
            long amount = entry.getValue().getAmount();
            double percent = Math.round((float) amount / totalMinutes * 100);
            categoryData.add(new CategoryData(category, (int) amount, percent));
        }
        return categoryData;
    }

    @PostMapping("/api/compare")
    public ResponseEntity<ComparingResponse> getStatisticsForComparingByClassifiedGoal(@RequestBody ComparingRequest request) {
        Long userId = tokenProcessor.parseToken(request.getToken());
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        List<ActivityRecord> activityRecords = activityRecordRepository.findAllByUserId(user.getId());
        Map<String, StatistcsData> statisticsByCategory = statisticsCalculator.getStatisticsByCategory(activityRecords);
        long userTotalMinutes = statisticsByCategory.values().stream()
                .mapToLong(StatistcsData::getAmount)
                .sum();
        List<CategoryData> myData = getCategoryData(statisticsByCategory, userTotalMinutes);

        List<UserTimeData> userTimeData = new ArrayList<>();

        List<User> users = userRepository.findAllByClassifiedGoal(user.getClassifiedGoal());
        users.remove(user);

        for (User target : users) {
            List<ActivityRecord> targetActivityRecords = activityRecordRepository.findAllByUserId(target.getId());
            Map<String, StatistcsData> targetStatisticsByCategory = statisticsCalculator.getStatisticsByCategory(targetActivityRecords);
            long targetTotalMinutes = targetStatisticsByCategory.values().stream()
                    .mapToLong(StatistcsData::getAmount)
                    .sum();
            List<CategoryData> targetCategoryData = getCategoryData(targetStatisticsByCategory, targetTotalMinutes);
            userTimeData.add(
                    new UserTimeData(target.getUsername(), (int) targetTotalMinutes, target.getGoal(), targetCategoryData)
            );
        }

        userTimeData.sort(Comparator.comparing(UserTimeData::getTotalMinutes).reversed());
        userTimeData.addFirst(new UserTimeData(user.getUsername(), (int) userTotalMinutes, user.getGoal(), myData));

        return ResponseEntity.ok(new ComparingResponse(true, userTimeData));
    }

}



