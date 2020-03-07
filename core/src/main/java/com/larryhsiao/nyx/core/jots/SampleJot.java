package com.larryhsiao.nyx.core.jots;

/**
 * Sample data of Jot
 */
public class SampleJot implements Jot {
    @Override
    public long id() {
        return -1;
    }

    @Override
    public String content() {
        return "This is sample string of Jot.";
    }

    @Override
    public double[] location() {
        return new double[]{0.0, 0.0};
    }

    @Override
    public long createdTime() {
        return System.currentTimeMillis();
    }

    @Override
    public String mood() {
        return new String(Character.toChars(0x1F600));
    }
}
