package org.project.timetracker.auth;

import lombok.Getter;


@Getter
public class LoginResponse {
    private final Boolean success;
    private final String email;
    private final String message;
    private final String token;

    public LoginResponse(Boolean success, String email, String message, String token) {
        this.success = success;
        this.email = email;
        this.message = message;
        this.token = token;
    }
}
