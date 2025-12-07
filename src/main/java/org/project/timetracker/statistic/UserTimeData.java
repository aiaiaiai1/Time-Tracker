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

    public UserTimeData(String username, int totalMinutes, String goal, List<CategoryData> categories) {
        this.username = username;
        this.totalMinutes = totalMinutes;
        this.goal = goal;
        this.categories = categories;
    }
}
