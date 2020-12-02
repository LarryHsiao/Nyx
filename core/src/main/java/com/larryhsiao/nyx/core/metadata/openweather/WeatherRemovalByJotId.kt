package com.larryhsiao.nyx.core.metadata.openweather

import com.larryhsiao.nyx.core.metadata.Metadata
import com.larryhsiao.clotho.Action
import com.larryhsiao.clotho.Source
import java.sql.Connection

/**
 * Action to remove all weather metadata by jot id.
 */
class WeatherRemovalByJotId(
    private val db: Source<Connection>,
    private val jotId: Long
) : Action {
    override fun fire() {
        db.value().prepareStatement(
            // language=H2
            """//
UPDATE METADATA
SET DELETED=1
WHERE TYPE = ?
  AND JOT_ID = ?"""
        ).use {
            it.setString(1, Metadata.Type.OPEN_WEATHER.name)
            it.setLong(2, jotId)
            it.executeUpdate()
        }
    }
}