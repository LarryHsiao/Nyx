package com.larryhsiao.nyx.azure

import com.silverhetch.clotho.Source
import okhttp3.*
import java.lang.IllegalStateException

/**
 * Source of translated string.
 */
class IssueToken(
    private val secret: String
) : Source<String> {
    override fun value(): String {
        return OkHttpClient().newCall(
            Request.Builder()
                .url("https://jotted.cognitiveservices.azure.com/sts/v1.0/issuetoken")
                .post(RequestBody.create(null, ""))
                .headers(
                    Headers.of(mapOf(Pair("Ocp-Apim-Subscription-Key", secret)))
                ).build()
        ).execute().use {
            if (it.code() == 200) {
                it.body()?.string() ?: ""
            } else {
                throw IllegalStateException("Can not fetch auth code from Azure.")
            }
        }
    }
}