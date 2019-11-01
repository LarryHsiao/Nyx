package com.larryhsiao.nyx.weather

import com.google.gson.JsonParser
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
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
        try {
            OpenWeatherWeather(JsonParser().parse("{}")).temperatureC()
            assertTrue(true)
        } catch (e: Exception) {
            fail("Should not have any exception ")
        }
    }
}