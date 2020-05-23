package com.larryhsiao.nyx.core.jots.filter;

/**
 * Constant object of {@link Filter}.
 */
public class ConstFilter implements Filter {
    private final long[] dateRange;
    private final String keyword;

    public ConstFilter(long[] dateRange, String keyword) {
        this.dateRange = dateRange;
        this.keyword = keyword;
    }

    @Override
    public long[] dateRange() {
        return dateRange;
    }

    @Override
    public String keyword() {
        return keyword;
    }
}
