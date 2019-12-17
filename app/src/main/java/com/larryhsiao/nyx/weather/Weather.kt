package com.larryhsiao.nyx.weather

/**
 * Represent the weather we get.
 */
interface Weather {
    /**
     * The weather icon Url
     */
    fun iconUrl(): String

    /**
     * The temperature in Celsius
     */
    fun temperatureC(): Float

    /**
     * Raw data of this weather
     */
    fun raw():String
}