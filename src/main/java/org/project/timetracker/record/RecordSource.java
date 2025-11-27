package org.project.timetracker.record;

public enum RecordSource {
    USER(1),
    GPS(2),
    CALENDAR(3);

    private final int priority;

    RecordSource(int priority) {
        this.priority = priority;
    }

    public int getPriority() {
        return priority;
    }
}
