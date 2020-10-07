package com.larryhsiao.nyx.core

import com.silverhetch.clotho.Source

/**
 * Source to build hash tag list from given string.
 */
class StringHashTags(
    private val original: String
) : Source<Set<String>> {
    override fun value(): Set<String> {
        return hashSetOf<String>().apply {
            val pattern = "^#(\\S+)".toPattern()
            original.lines().forEach {
                val matcher = pattern.matcher(it)
                while (matcher.find()) {
                    add(matcher.group(1) ?: continue)
                }
            }
        }
    }
}