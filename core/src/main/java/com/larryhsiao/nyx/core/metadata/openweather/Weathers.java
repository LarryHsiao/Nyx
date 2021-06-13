package com.larryhsiao.nyx.core.metadata.openweather;

import com.larryhsiao.clotho.openweather.Weather;

public interface Weathers {
    void update(long jotId, Weather weather);

    void removeByJotId(long id);
}
