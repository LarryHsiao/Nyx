package com.larryhsiao.nyx.weather.room;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;

import java.util.List;

/**
 * Dao that access the weather related tables.
 * <p>
 * Note: This is designed for weather in diary, currently no direct modification to
 * the weather table.
 */
@Dao
public abstract class WeatherDao {
    /**
     * Insert a row to table.
     */
    @Insert
    public abstract long create(WeatherEntity entity);

    /**
     * Update the diary for relation to weather.
     */
    @Query("UPDATE diary SET weather_id=:weatherId WHERE id=:diaryId")
    public abstract void attachWeather(long diaryId, long weatherId);

    @Query("SELECT * FROM weather;")
    public abstract List<WeatherEntity> all();

    /**
     * Convenient transaction to create attached weather to dairy.
     */
    @Transaction
    public void newWeatherToDiary(long diaryId, WeatherEntity weather) {
        final long weatherId = create(weather);
        attachWeather(diaryId, weatherId);
    }
}
