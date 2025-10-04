package org.project.timetracker.global;


import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ExceptionResponse {

    private final Boolean success = false;
    private final String message;

}
