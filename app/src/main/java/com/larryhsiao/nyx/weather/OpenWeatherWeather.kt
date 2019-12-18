package com.larryhsiao.nyx.weather

import com.google.gson.JsonArray
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
    private val weathersArray by lazy {
        rootObj.getAsJsonArray("weather") ?: JsonArray()
    }

    private val tempCelsius by lazy {
        mainObj.get("temp")?.asFloat ?: 0f
    }

    private val iconCode by lazy {
        if (weathersArray.size() > 0) {
            weathersArray.get(0).asJsonObject.get("icon")?.asString ?: ""
        } else {
            ""
        }
    }

    override fun temperatureC(): Float {
        return tempCelsius
    }

    override fun iconUrl(): String {
        return """https://openweathermap.org/img/wn/$iconCode@2x.png"""
    }

    override fun raw(): String {
        return root.toString()
    }
}