package org.project.timetracker.statistic;

import lombok.Getter;

@Getter
public class StatisticsDataResponse {

    private String category;
    private int frequency;
    private String amount;
    private double timePercent;
    private double accordPercent;

    public StatisticsDataResponse(String category, int frequency, String amount, double timePercent, double accordPercent) {
        this.category = category;
        this.frequency = frequency;
        this.amount = amount;
        this.timePercent = timePercent;
        this.accordPercent = accordPercent;
    }
}
