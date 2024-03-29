package com.larryhsiao.nyx.core.jots;

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
     * Replace exist jot without increasing version.
     */
    void replace(Jot jot);

    /**
     * Create new jot with defaults time and versions.
     */
    Jot create(Jot jot);

    /**
     * Create a jot into db with specific id.
     */
    void createWithId(Jot jot);

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
