package org.project.timetracker.statistic;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Getter
public class StatisticsDetailResponse {

    private String name;
    private String amount;
    private int frequency;

    public StatisticsDetailResponse(String name, String amount, int frequency) {
        this.name = name;
        this.amount = amount;
        this.frequency = frequency;
    }
}
