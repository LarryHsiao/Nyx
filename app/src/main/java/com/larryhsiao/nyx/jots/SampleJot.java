package com.larryhsiao.nyx.jots;

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
    public long createdTime() {
        return System.currentTimeMillis();
    }
}
