package com.larryhsiao.nyx.core.jots.moods;

/**
 * Constant of {@link RankedMood}
 */
public class ConstRankedMood implements RankedMood {
    private final String mood;
    private final int usedTimes;

    public ConstRankedMood(String mood, int usedTimes) {
        this.mood = mood;
        this.usedTimes = usedTimes;
    }

    @Override
    public String mood() {
        return mood;
    }

    @Override
    public int usedTimes() {
        return usedTimes;
    }
}
