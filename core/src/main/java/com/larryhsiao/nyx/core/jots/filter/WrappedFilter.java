package com.larryhsiao.nyx.core.jots.filter;

/**
 * Wrapped object of {@link Filter}
 */
public class WrappedFilter implements Filter {
    private final Filter filter;

    public WrappedFilter(Filter filter) {
        this.filter = filter;
    }

    @Override
    public String keyword() {
        return filter.keyword();
    }

    @Override
    public long[] dateRange() {
        return filter.dateRange();
    }
}
