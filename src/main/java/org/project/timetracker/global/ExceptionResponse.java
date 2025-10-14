package org.project.timetracker.global;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class ExceptionResponse {

    private final Boolean success = false;
    private final String message;

}
