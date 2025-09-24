package org.project.timetracker.auth;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class RegisterResponse {

    private final Boolean success;
    private final String message;
    private final String token;
}
