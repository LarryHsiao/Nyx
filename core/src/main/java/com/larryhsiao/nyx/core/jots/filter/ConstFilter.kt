package com.larryhsiao.nyx.core.jots.filter;

/**
 * Constant object of {@link Filter}.
 */
public class ConstFilter implements Filter {
    private final long[] dateRange;
    private final String keyword;
    private final long[] ids;

    public ConstFilter() {
        this(new long[]{0L, 0L}, "", new long[0]);
    }

    public ConstFilter(long[] dateRange, String keyword, long[] ids) {
        this.dateRange = dateRange;
        this.keyword = keyword;
        this.ids = ids;
    }

    @Override
    public long[] dateRange() {
        return dateRange;
    }

    @Override
    public String keyword() {
        return keyword;
    }

    @Override
    public long[] ids() {
        return ids;
    }
}
