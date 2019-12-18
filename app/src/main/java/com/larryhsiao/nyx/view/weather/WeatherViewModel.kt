package com.larryhsiao.nyx.view.weather

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.larryhsiao.nyx.weather.Weather
import com.larryhsiao.nyx.weather.WeatherByGeo
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import com.larryhsiao.nyx.BuildConfig
import com.larryhsiao.nyx.database.RDatabase
import com.larryhsiao.nyx.weather.room.WeatherEntity

/**
 * ViewModel for weather fetching
 */
class WeatherViewModel(private val app: Application) : AndroidViewModel(app) {
    private val db = RDatabase.Singleton(app).value()
    private val weather: MutableLiveData<Weather> = MutableLiveData()

    /**
     * The live data of weather.
     */
    fun weather(): LiveData<Weather> {
        return weather
    }

    /**
     * Trigger querying the weather from open weather by geo.
     */
    fun byGeo(latitude: Double, longitude: Double) {
        GlobalScope.launch {
            weather.postValue(
                WeatherByGeo(
                    BuildConfig.OPEN_WEATHER_API_KEY,
                    latitude,
                    longitude,
                    app.resources.configuration.locale
                ).value()
            )
        }
    }

    /**
     * Attach the weather to given diary
     */
    fun attachToDiary(diaryId: Long) {
        GlobalScope.launch {
            weather.value?.let {
                db.weatherDao().newWeatherToDiary(
                    diaryId, WeatherEntity(
                        0,
                        it.iconUrl(),
                        it.raw()
                    )
                )
            }
        }
    }
}
