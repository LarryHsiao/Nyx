package com.larryhsiao.nyx.core.jots.filter

/**
 * Filter object
 */
interface Filter {
    /**
     * @return Specific Ids for filtering the Jots.
     */
    fun ids(): LongArray?

    /**
     * @return Two long to represent date range.
     */
    fun dateRange(): LongArray?

    /**
     * @return The keyword to search.
     */
    fun keyword(): String?
}