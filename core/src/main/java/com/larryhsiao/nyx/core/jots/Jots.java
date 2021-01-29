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

    /**
     * Create new jot.
     */
    Jot newJot(Jot jot);

    /**
     * Update the given jot.
     */
    void updateJot(Jot jot);

    /**
     * Delete the given jot by id.
     */
    void deleteJotById(long id);
}
