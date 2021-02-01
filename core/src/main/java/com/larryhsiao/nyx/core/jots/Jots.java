package com.larryhsiao.nyx.core.jots;

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
    void update(Jot jot);

    /**
     * Delete the given jot by id.
     */
    void deleteById(long id);
}
