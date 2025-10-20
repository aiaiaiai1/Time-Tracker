package org.project.timetracker.statistic;

public class StatistcsData {

    private int frequency;
    private long amount;

    public StatistcsData(int frequency, long amount) {
        this.frequency = frequency;
        this.amount = amount;
    }

    public int getFrequency() {
        return frequency;
    }

    public long getAmount() {
        return amount;
    }
}
