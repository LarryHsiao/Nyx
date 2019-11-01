package com.larryhsiao.nyx.weather

import com.google.gson.JsonElement
import com.google.gson.JsonObject

/**
 * Implementation of [Weather] with Open Weather API response.
 */
class OpenWeatherWeather(private val root: JsonElement) : Weather {
    private val rootObj by lazy {
        root.asJsonObject ?: JsonObject()
    }
    private val mainObj by lazy {
        rootObj.getAsJsonObject("main") ?: JsonObject()
    }
    private val weatherObj by lazy {
        rootObj.getAsJsonObject("weather") ?: JsonObject()
    }

    private val tempCelsius by lazy {
        mainObj.get("temp")?.asFloat ?: 0f
    }

    private val iconCode by lazy {
        weatherObj.get("icon")?.asString ?: ""
    }

    override fun temperatureC(): Float {
        return tempCelsius
    }

    override fun iconUrl(): String {
        return """http://openweathermap.org/img/wn/$iconCode@2x.png"""
    }
}