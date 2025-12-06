package org.project.timetracker.statistic;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class ComparingRequest {

    private String token;
    private Long userId;

    public ComparingRequest(String token, Long userId) {
        this.token = token;
        this.userId = userId;
    }
}
