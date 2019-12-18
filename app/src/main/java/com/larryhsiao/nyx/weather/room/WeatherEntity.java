package com.larryhsiao.nyx.weather.room;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Entity of weather
 */
@Entity(tableName = "weather")
public class WeatherEntity {
    @PrimaryKey(autoGenerate = true)
    public final long id;

    public final String iconUrl;

    /**
     * The raw data of weather.
     */
    @NonNull
    public final String raw;

    public WeatherEntity(long id, String iconUrl, @NonNull String raw) {
        this.id = id;
        this.iconUrl = iconUrl;
        this.raw = raw;
    }
}
