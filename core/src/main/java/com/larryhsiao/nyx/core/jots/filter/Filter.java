package com.larryhsiao.nyx.core.jots.filter;

/**
 * Filter object
 */
public interface Filter {
    /**
     * @return Two long to represent date range.
     */
    long[] dateRange();

    /**
     * @return The keyword to search.
     */
    String keyword();
}
