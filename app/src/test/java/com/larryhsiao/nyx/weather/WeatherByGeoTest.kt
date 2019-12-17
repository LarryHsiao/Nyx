package com.larryhsiao.nyx.weather

import com.larryhsiao.nyx.BuildConfig
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.util.*

/**
 * Test for WeatherByGeo class
 */
@RunWith(RobolectricTestRunner::class)
class WeatherByGeoTest {
    /**
     * Check if API available
     */
    @Test
    fun simple() {
        assertNotEquals(
            0f,
            WeatherByGeo(
                BuildConfig.OPEN_WEATHER_API_KEY,
                20.0,
                120.0,
                Locale.getDefault()
            ).value().temperatureC()
        )
    }
}