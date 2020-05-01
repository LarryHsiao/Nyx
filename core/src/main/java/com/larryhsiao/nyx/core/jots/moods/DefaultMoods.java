package com.larryhsiao.nyx.core.jots.moods;

import com.silverhetch.clotho.Source;

import java.util.Arrays;
import java.util.List;

/**
 * Default moods.
 */
public class DefaultMoods implements Source<List<String>> {
    @Override
    public List<String> value() {
        return Arrays.asList(
            new String(Character.toChars(0x1F603)),
            new String(Character.toChars(0x1F601)),
            new String(Character.toChars(0x1F602)),
            new String(Character.toChars(0x1F642)),
            new String(Character.toChars(0x1F970)),
            new String(Character.toChars(0x1F60D)),
            new String(Character.toChars(0x1F60B)),
            new String(Character.toChars(0x1F60F)),
            new String(Character.toChars(0x1F612)),
            new String(Character.toChars(0x1F928)),
            new String(Character.toChars(0x1F611)),
            new String(Character.toChars(0x1F614)),
            new String(Character.toChars(0x1F634)),
            new String(Character.toChars(0x1F912)),
            new String(Character.toChars(0x1F927)),
            new String(Character.toChars(0x1F976)),
            new String(Character.toChars(0x1F974)),
            new String(Character.toChars(0x1F973)),
            new String(Character.toChars(0x1F61D))
        );
    }
}
