package com.larryhsiao.nyx.core.jots;

import org.jetbrains.annotations.NotNull;

import java.util.Calendar;
import java.util.List;

/**
 * Jots of the Nyx app have.
 */
public interface Jots {
    /**
     * All Jots.
     */
    List<Jot> all();

    Jot byId(long id);

    /**
     * Create new jot.
     */
    Jot create(Jot jot);

    /**
     * Update the given jot.
     */
    Jot update(Jot jot);

    /**
     * Delete the given jot by id.
     */
    void deleteById(long id);

    List<Jot> byDateRange(Calendar from, Calendar to);

    Jot createByTimeSpace(
        Calendar time,
        double[] longLat,
        int offset
    );

    List<Jot> byKeyword(String keyword);

    List<Jot> byIds(long[] ids);
}
