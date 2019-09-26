package com.larryhsiao.nyx.placeholder

import com.silverhetch.clotho.Source
import java.util.regex.Pattern

/**
 * Determine the given string is a ipv4 address.
 */
@Deprecated("Use clotho version")
class IsIPv4(private val address: String) : Source<Boolean> {
    private val pattern by lazy {
        Pattern.compile(
            """^(([01]?\d\d?|2[0-4]\d|25[0-5])\.){3}([01]?\d\d?|2[0-4]\d|25[0-5])$"""
        )
    }

    override fun value(): Boolean {
        return pattern.matcher(address).find()
    }
}