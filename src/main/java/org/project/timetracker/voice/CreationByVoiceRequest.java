package org.project.timetracker.voice;

public record CreationByVoiceRequest(
        String token,
        String voice
) {
}
