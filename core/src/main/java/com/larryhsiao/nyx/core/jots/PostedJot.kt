package com.larryhsiao.nyx.core.jots

import com.silverhetch.clotho.Source
import java.sql.Connection
import java.util.*

/**
 * Source to build a Jot that just updated or created.
 */
class PostedJot @JvmOverloads constructor(
    private val db: Source<Connection>,
    private val jot: Jot,
    private val updateVer: Boolean = true
) : Source<Jot?> {
    override fun value(): Jot {
        return if (jot.id() == -1L) {
            val calendar = Calendar.getInstance()
            calendar.time = Date(jot.createdTime())
            NewJot(
                db,
                jot.title(),
                jot.content(),
                jot.location(),
                calendar,
                jot.mood()
            ).value()
        } else {
            UpdateJot(jot, db, updateVer).fire()
            jot
        }
    }
}