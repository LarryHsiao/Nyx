package com.larryhsiao.nyx.core.metadata;

import com.larryhsiao.nyx.core.metadata.openweather.Weathers;

import java.util.List;

public interface MetadataSet {
    List<Metadata> byJotId(long id);

    void deleteByJotId(long id);

    Weathers weathers();
}
