package org.project.timetracker.statistic;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class ComparingRequest {

    private String token;

    public ComparingRequest(String token) {
        this.token = token;
    }
}
