package com.larryhsiao.nyx.core.jots.moods;

import com.silverhetch.clotho.Source;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Source to build moods for user to pick.
 */
public class MergedMoods implements Source<List<String>> {
    private final Source<List<String>> ranked;
    private final Source<List<String>> defaultSrc;

    public MergedMoods(Source<List<String>> ranked, Source<List<String>> defaultSrc) {
        this.ranked = ranked;
        this.defaultSrc = defaultSrc;
    }

    @Override
    public List<String> value() {
        Set<String> result = new LinkedHashSet<>(ranked.value());
        final List<String> defaultMoods = defaultSrc.value();
        result.addAll(defaultMoods);
        return new ArrayList<>(result).subList(0, defaultMoods.size() - 1);
    }
}
