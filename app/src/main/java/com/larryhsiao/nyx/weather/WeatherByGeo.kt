package com.larryhsiao.nyx.weather

import android.net.Uri
import com.google.gson.JsonParser
import com.google.gson.stream.JsonReader
import com.silverhetch.clotho.Source
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.InputStreamReader
import java.util.*

/**
 * Source to build a [Weather] object with geo.
 */
class WeatherByGeo(
    private val apiKey: String,
    private val latitude: Double,
    private val longitude: Double,
    private val locale: Locale = Locale.getDefault()
) : Source<Weather> {
    companion object {
        private const val ENDPOINT =
            "https://api.openweathermap.org/data/2.5/weather"
    }

    override fun value(): Weather {
        with(
            OkHttpClient().newCall(
                Request.Builder()
                    .url(
                        Uri.parse(ENDPOINT)
                            .buildUpon()
                            .appendQueryParameter("lat", latitude.toString())
                            .appendQueryParameter("lon", longitude.toString())
                            .appendQueryParameter("lang", locale.language)
                            .appendQueryParameter("APPID", apiKey)
                            .build().toString()
                    )
                    .build()
            ).execute()
        ) {
            if (!isSuccessful) {
                throw RuntimeException(String(body()?.bytes() ?: ByteArray(0)))
            }
            return OpenWeatherWeather(
                JsonParser().parse(
                    this.body()?.let {
                        JsonReader(InputStreamReader(it.byteStream()))
                    }
                ).asJsonObject
            )
        }
    }
}