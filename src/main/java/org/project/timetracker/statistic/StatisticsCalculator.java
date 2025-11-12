package org.project.timetracker.statistic;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.project.timetracker.record.ActivityRecord;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StatisticsCalculator {
    public Map<String, StatistcsData> getStatisticsByCategory(List<ActivityRecord> activityRecords) {

        Map<String, List<ActivityRecord>> groupBy = activityRecords.stream()
                .collect(Collectors.groupingBy(ActivityRecord::getCategory));

        Map<String, StatistcsData> results = new HashMap<>();

        for (Map.Entry<String, List<ActivityRecord>> maps : groupBy.entrySet()) {
            String category = maps.getKey();
            List<ActivityRecord> records = maps.getValue();
            int frequency = records.size();
            long amount = records.stream().mapToLong(ActivityRecord::getSpanMinutes)
                    .sum();
            results.put(category, new StatistcsData(frequency, amount));
        }
        return results;
    }

    public Map<String, Map<String, StatisticsDetailData>> getStatisticsDetailsByName(List<ActivityRecord> activityRecords) {
        Map<String, List<ActivityRecord>> groupByCategory = activityRecords.stream()
                .collect(Collectors.groupingBy(ActivityRecord::getCategory));

        Map<String, Map<String,StatisticsDetailData>> finalResults = new HashMap<>();


        for (Map.Entry<String, List<ActivityRecord>> maps : groupByCategory.entrySet()) {
            String category = maps.getKey();
            List<ActivityRecord> records = maps.getValue();

            Map<String, StatisticsDetailData> results = new HashMap<>();

            Map<String, List<ActivityRecord>> groupByName = records.stream()
                    .collect(Collectors.groupingBy(ActivityRecord::getMemo));

            for (Map.Entry<String, List<ActivityRecord>> groupByNameMaps : groupByName.entrySet()) {
                String name = groupByNameMaps.getKey();
                List<ActivityRecord> values = maps.getValue();
                int frequency = values.size();
                long amount = records.stream().mapToLong(ActivityRecord::getSpanMinutes)
                        .sum();

                results.put(name, new StatisticsDetailData(frequency, amount));
            }
            finalResults.put(category, results);
        }
        return finalResults;
    }
    }
