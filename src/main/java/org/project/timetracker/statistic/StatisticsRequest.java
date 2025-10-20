package org.project.timetracker.statistic;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class StatisticsRequest {

    private String token;
    private String startDate;
    private String endDate;

    public StatisticsRequest(String token, String startDate, String endDate) {
        this.token = token;
        this.startDate = startDate;
        this.endDate = endDate;
    }
}
