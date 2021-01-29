package com.larryhsiao.nyx.core.jots;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Memory implementation of {@link Jots}.
 */
public class MemoryJots implements Jots {
    private final Map<Long, Jot> jots = new HashMap<>();

    @Override
    public List<Jot> all() {
        return new ArrayList<>(jots.values());
    }

    @Override
    public Jot newJot(Jot jot) {
        jots.put(jot.id(), jot);
        return jot;
    }

    @Override
    public void updateJot(Jot jot) {
        jots.put(jot.id(), jot);
    }

    @Override
    public void deleteJotById(long id) {
        jots.remove(id);
    }
}
