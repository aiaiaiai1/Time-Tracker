package org.project.timetracker.statistic;

import lombok.Getter;

import java.util.List;

@Getter
public class StatisticsDataResponse {

    private String category;
    private int frequency;
    private String amount;
    private double timePercent;
    private double accordPercent;
    private List<StatisticsDetailResponse> details;

    public StatisticsDataResponse(
            String category, int frequency, String amount, double timePercent,
            double accordPercent, List<StatisticsDetailResponse> details
    ) {
        this.category = category;
        this.frequency = frequency;
        this.amount = amount;
        this.timePercent = timePercent;
        this.accordPercent = accordPercent;
        this.details = details;
    }
}
