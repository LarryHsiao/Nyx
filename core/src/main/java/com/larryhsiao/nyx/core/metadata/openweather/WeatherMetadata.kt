package com.larryhsiao.nyx.core.metadata.openweather

import com.larryhsiao.clotho.openweather.Weather
import com.larryhsiao.nyx.core.metadata.Metadata
import java.math.BigDecimal

class WeatherMetadata(
    private val jotId: Long,
    private val weather: Weather
) : Metadata {
    override fun id(): Long = -1
    override fun type(): Metadata.Type = Metadata.Type.OPEN_WEATHER
    override fun title(): String = ""
    override fun value(): String = weather.toString()
    override fun valueBigDecimal(): BigDecimal = BigDecimal.ZERO
    override fun comment(): String = ""
    override fun jotId(): Long = jotId
    override fun version(): Long = 1
    override fun deleted(): Boolean = false
}