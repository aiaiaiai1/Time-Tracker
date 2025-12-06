package org.project.timetracker.statistic;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class ComparingResponse {

    private boolean success;
    private List<UserTimeData> data;

    public ComparingResponse(boolean success, List<UserTimeData> data) {
        this.success = success;
        this.data = data;
    }
}
