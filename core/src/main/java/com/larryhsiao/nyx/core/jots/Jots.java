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
}
