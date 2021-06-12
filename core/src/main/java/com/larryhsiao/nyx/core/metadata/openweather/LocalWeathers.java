package com.larryhsiao.nyx.core.metadata.openweather;

import com.larryhsiao.clotho.Source;
import com.larryhsiao.clotho.openweather.Weather;

import java.sql.Connection;

public class LocalWeathers implements Weathers {
    private final Source<Connection> db;

    public LocalWeathers(Source<Connection> db) {this.db = db;}

    @Override
    public void update(long jotId, Weather weather) {
       new  PostedWeatherMeta(db, weather, jotId).value();
    }

    @Override
    public void removeByJotId(long id) {
       new  WeatherRemovalByJotId(db, id).fire();
    }
}
