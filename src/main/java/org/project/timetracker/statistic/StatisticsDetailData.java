package org.project.timetracker.statistic;

public class StatisticsDetailData {

    private int frequency;
    private long amount;

    public StatisticsDetailData(int frequency, long amount) {
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
