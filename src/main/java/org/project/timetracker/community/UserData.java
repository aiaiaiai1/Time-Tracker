package org.project.timetracker.community;

public record UserData(Long userId, String nickname) {

    public UserData(Long userId, String nickname) {
        this.userId = userId;
        this.nickname = nickname.substring(0, 2) + "***";
    }
}

