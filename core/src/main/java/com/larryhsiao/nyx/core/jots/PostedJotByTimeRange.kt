package com.larryhsiao.nyx.core.jots

import com.larryhsiao.clotho.Source
import java.sql.Connection
import java.util.*

/**
 * Source to build a Jot that in range of given time.
 *
 * @param offset The time offset of range. i.e. The valid time range of a jot is offset * 2.
 */
class PostedJotByTimeRange(
    private val db: Source<Connection>,
    private val anchorTime: Calendar,
    private val anchorLocation: DoubleArray,
    private val offset: Int
) : Source<Jot> {
    override fun value(): Jot {
        val sameGroupJots = QueriedJots(
            JotsByTimestampRange(
                db,
                Calendar.getInstance().apply {
                    timeInMillis = anchorTime.timeInMillis
                    add(Calendar.MILLISECOND, -offset)
                }.timeInMillis,
                Calendar.getInstance().apply {
                    timeInMillis = anchorTime.timeInMillis
                    add(Calendar.MILLISECOND, offset)
                }.timeInMillis
            )
        ).value()
            .sortedBy { it.createdTime() }
            .reversed()
        return if (sameGroupJots.isNotEmpty()) {
            sameGroupJots[0]
        } else {
            NewJot(
                db,
                "",
                "",
                anchorLocation,
                anchorTime,
                "",
                false
            ).value()
        }
    }
}