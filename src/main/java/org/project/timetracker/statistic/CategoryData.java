package org.project.timetracker.statistic;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CategoryData {
    private String category;
    private int minutes;
    private double percent;

    public CategoryData(String category, int minutes, double percent) {
        this.category = category;
        this.minutes = minutes;
        this.percent = percent;
    }

    @Override
    public String toString() {
        return "CategoryData{" +
                "category='" + category + '\'' +
                ", minutes=" + minutes +
                ", percent=" + percent +
                '}';
    }
}
