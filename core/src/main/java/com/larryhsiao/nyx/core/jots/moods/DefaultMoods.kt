package com.larryhsiao.nyx.core.jots.moods

import com.silverhetch.clotho.Source

/**
 * Default moods.
 */
class DefaultMoods : Source<List<String>> {
    override fun value(): List<String> {
        return listOf(
            String(Character.toChars(0x1F603)),
            String(Character.toChars(0x1F601)),
            String(Character.toChars(0x1F602)),
            String(Character.toChars(0x1F642)),
            String(Character.toChars(0x1F970)),
            String(Character.toChars(0x1F60D)),
            String(Character.toChars(0x1F60B)),
            String(Character.toChars(0x1F60F)),
            String(Character.toChars(0x1F612)),
            String(Character.toChars(0x1F928)),
            String(Character.toChars(0x1F611)),
            String(Character.toChars(0x1F614)),
            String(Character.toChars(0x1F634)),
            String(Character.toChars(0x1F912)),
            String(Character.toChars(0x1F927)),
            String(Character.toChars(0x1F976)),
            String(Character.toChars(0x1F974)),
            String(Character.toChars(0x1F973)),
            String(Character.toChars(0x1F61D))
        )
    }
}