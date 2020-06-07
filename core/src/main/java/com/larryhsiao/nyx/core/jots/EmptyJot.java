package com.larryhsiao.nyx.core.jots;

import static java.lang.Double.MIN_VALUE;

/**
 * Empty jot.
 */
public class EmptyJot extends ConstJot {
    public EmptyJot() {
        super(
            -1,
            "",
            System.currentTimeMillis(),
            new double[]{MIN_VALUE, MIN_VALUE},
            "",
            1,
            false
        );
    }
}
