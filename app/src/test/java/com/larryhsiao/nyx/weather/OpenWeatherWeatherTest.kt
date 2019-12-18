package com.larryhsiao.nyx.weather

import com.google.gson.JsonParser
import org.junit.Test

/**
 * Unit-test for the class [OpenWeatherWeather]
 */
class OpenWeatherWeatherTest {
    /**
     * Test all method with wrong format raw data.
     */
    @Test
    fun wrongFormat() {
        OpenWeatherWeather(JsonParser().parse("{}")).temperatureC()
    }
}