package com.larryhsiao.nyx.core.jots.filter

/**
 * Wrapped object of [Filter]
 */
open class WrappedFilter(private val filter: Filter) : Filter {
    override fun keyword(): String? {
        return filter.keyword()
    }

    override fun dateRange(): LongArray? {
        return filter.dateRange()
    }

    override fun ids(): LongArray? {
        return filter.ids()
    }
}