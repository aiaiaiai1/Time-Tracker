package org.project.timetracker.auth;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class RegisterResponse {

    private final Boolean success;
    private final String message;
    private final String token;
}
