package org.project.timetracker.statistic;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class UserTimeData {
    private String username;
    private int totalMinutes;
    private String goal;
    private List<CategoryData> categories;
    private String summary;


    public UserTimeData(String username, int totalMinutes, String goal, List<CategoryData> categories, String summary) {
        this.username = username;
        this.totalMinutes = totalMinutes;
        this.goal = goal;
        this.categories = categories;
        this.summary = summary;
    }
}
