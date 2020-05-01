package com.larryhsiao.nyx.core.jots.moods;

import com.larryhsiao.nyx.core.jots.AllJots;
import com.larryhsiao.nyx.core.jots.Jot;
import com.larryhsiao.nyx.core.jots.QueriedJots;
import com.silverhetch.clotho.Source;
import com.silverhetch.clotho.utility.comparator.StringComparator;

import java.sql.Connection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Source to build moods by used ranking.
 */
public class RankedMoods implements Source<List<RankedMood>> {
    private final Source<Connection> db;

    public RankedMoods(Source<Connection> db) {
        this.db = db;
    }

    @Override
    public List<RankedMood> value() {
        final Map<String, Integer> result = new HashMap<>();
        for (Jot jot : new QueriedJots(new AllJots(db)).value()) {
            if (jot.mood().length() < 2 || jot.mood().substring(0,1).contains(" ")){
                continue;
            }
            if (result.containsKey(jot.mood())) {
                result.put(jot.mood(), result.get(jot.mood()) + 1);
            } else {
                result.put(jot.mood(), 1);
            }
        }
        Comparator<String> strComparator = new StringComparator();
        return result.entrySet().stream()
            .map((it) -> new ConstRankedMood(
                it.getKey(),
                it.getValue()
            ))
            .sorted((constRankedMood, t1) -> {
                int value = t1.usedTimes() - constRankedMood.usedTimes();
                if (value == 0) {
                    return strComparator.compare(t1.mood(), constRankedMood.mood());
                }
                return value;
            })
            .collect(Collectors.toList());
    }
}
