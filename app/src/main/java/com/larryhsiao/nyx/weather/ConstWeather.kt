package com.larryhsiao.nyx.weather

/**
 * Const of Weather object.
 */
class ConstWeather(
    private val iconUrl: String,
    private val tempC: Float
) : Weather {
    override fun iconUrl(): String {
        return iconUrl
    }

    override fun temperatureC(): Float {
        return tempC
    }

    override fun raw(): String {
        return ""
    }
}