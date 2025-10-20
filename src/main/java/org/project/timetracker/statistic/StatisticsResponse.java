package org.project.timetracker.statistic;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Getter
public class StatisticsResponse {

    private Boolean success = true;
    private String message = "기록이 성공적으로 조회 되었습니다.";
    private List<StatisticsDataResponse> data;

    public StatisticsResponse(List<StatisticsDataResponse> dataResponse) {
        this.data = dataResponse;
    }
}
