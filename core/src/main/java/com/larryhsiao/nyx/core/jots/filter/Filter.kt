package com.larryhsiao.nyx.core.jots.filter;

/**
 * Filter object
 */
public interface Filter {
    /**
     * @return Specific Ids for filtering the Jots.
     */
    long[] ids();

    /**
     * @return Two long to represent date range.
     */
    long[] dateRange();

    /**
     * @return The keyword to search.
     */
    String keyword();
}
