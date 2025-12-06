package org.project.timetracker.community;

import java.util.List;

public record UserResponse(boolean success, String message, List<UserData> data) {
}
