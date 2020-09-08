package com.larryhsiao.nyx.core.jots.filter

/**
 * Constant object of [Filter].
 */
open class ConstFilter @JvmOverloads constructor(
    private val dateRange: LongArray = longArrayOf(0L, 0L),
    private val keyword: String = "",
    private val ids: LongArray = LongArray(0)
) : Filter {
    override fun dateRange(): LongArray? {
        return dateRange
    }

    override fun keyword(): String? {
        return keyword
    }

    override fun ids(): LongArray? {
        return ids
    }
}