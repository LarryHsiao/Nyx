package com.larryhsiao.nyx.core.jots.moods;

import com.larryhsiao.clotho.Source;
import com.larryhsiao.nyx.core.jots.Jot;

/**
 * Source to build json string from {@link Jot}.
 */
public class JotJson implements Source<String> {
    private final Jot jot;

    public JotJson(Jot jot) {this.jot = jot;}

    @Override
    public String value() {
        return null;
    }
}
