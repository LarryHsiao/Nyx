package com.larryhsiao.nyx.core.jots

import com.silverhetch.clotho.Source
import java.sql.Connection
import java.sql.Date
import java.sql.ResultSet
import java.sql.Timestamp
import java.util.*

/**
 * Source to build jot by given date.
 */
class JotsByDateRange(
    private val db: Source<Connection>,
    private val started: Calendar,
    private val ended: Calendar
) : Source<ResultSet> {
    override fun value(): ResultSet {
        return JotsByTimestampRange(
            db,
            started.apply {
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.timeInMillis,
            ended.apply {
                set(Calendar.HOUR_OF_DAY, 23)
                set(Calendar.MINUTE, 59)
                set(Calendar.SECOND, 59)
                set(Calendar.MILLISECOND, 999)
            }.timeInMillis
        ).value()
    }
}