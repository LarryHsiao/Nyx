package com.larryhsiao.nyx.core.jots

import com.larryhsiao.nyx.core.jots.filter.Filter
import com.larryhsiao.clotho.Source
import java.sql.Connection
import java.sql.ResultSet

/**
 * Source to select implementation to run with.
 */
class JotsByCheckedFilter(
    private val db: Source<Connection>,
    private val filter: Filter
) : Source<ResultSet> {
    override fun value(): ResultSet {
        val range = filter.dateRange()
        return if (range!![0] == 0L && range[1] == 0L) {
            JotsByKeyword(db, filter.keyword()).value()
        } else JotsByFilter(db, filter).value()
    }
}