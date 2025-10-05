package org.project.timetracker.auth;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class RegisterRequest {

    private String name;
    private String email;
    private String password;

}
