package com.larryhsiao.nyx.core.metadata.openweather

import com.larryhsiao.clotho.openweather.Weather
import com.larryhsiao.nyx.core.metadata.*
import com.larryhsiao.nyx.core.metadata.Metadata.Type.OPEN_WEATHER
import com.silverhetch.clotho.Source
import java.sql.Connection

/**
 * Source to build a Weather Metadata which have been stored into database.
 */
class PostedWeatherMeta(
    private val db: Source<Connection>,
    private val weather: Weather,
    private val jotId: Long
) : Source<Metadata> {
    override fun value(): Metadata {
        val existMetadata = QueriedMetadata(
            MetadataByJotId(db, jotId, true)
        ).value().filter { it.type() == OPEN_WEATHER }
        return if (existMetadata.isNotEmpty()) {
            UpdatedMetadata(
                db,
                object : WrappedMetadata(existMetadata[0]) {
                    override fun deleted(): Boolean = false
                }
            ).value()
        } else {
            CreatedMetadata(
                db,
                WeatherMetadata(jotId, weather)
            ).value()
        }
    }
}