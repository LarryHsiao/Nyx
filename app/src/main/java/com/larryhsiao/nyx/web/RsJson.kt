package com.larryhsiao.nyx.web

import org.takes.rs.RsWithBody
import org.takes.rs.RsWithStatus
import org.takes.rs.RsWithType
import org.takes.rs.RsWrap
import java.net.HttpURLConnection
import java.net.HttpURLConnection.HTTP_OK

/**
 * Json response with json string input.
 */
class RsJson(private val json: String) : RsWrap(
    RsWithType(
        RsWithStatus(RsWithBody(json), HTTP_OK),
        "application/json"
    )
)