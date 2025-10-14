package org.project.timetracker.auth;

public record DuplicateCheckResponse(
        boolean success,
        String message
) {
    public static DuplicateCheckResponse ofFail() {
        return new DuplicateCheckResponse(false, "이미 사용 중인 아이디입니다.");
    }

    public static DuplicateCheckResponse ofSuccess() {
        return new DuplicateCheckResponse(true, "사용 가능한 아이디입니다.");
    }
}
