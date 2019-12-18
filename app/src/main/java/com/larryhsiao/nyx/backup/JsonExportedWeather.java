package com.larryhsiao.nyx.backup;

import com.google.gson.Gson;
import com.larryhsiao.nyx.weather.room.WeatherDao;
import com.larryhsiao.nyx.weather.room.WeatherEntity;
import com.silverhetch.clotho.Source;

import java.util.ArrayList;
import java.util.List;

/**
 * Source to build exported json from sql.
 */
public class JsonExportedWeather implements Source<List<String>> {
    private final WeatherDao dao;

    public JsonExportedWeather(WeatherDao dao) {
        this.dao = dao;
    }

    @Override
    public List<String> value() {
        final Gson gson = new Gson();
        final List<String> result = new ArrayList<>();
        for (WeatherEntity weatherEntity : dao.all()) {
            result.add(gson.toJson(weatherEntity));
        }
        return result;
    }
}
