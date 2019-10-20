package com.larryhsiao.nyx.azure.translation

import android.net.Uri
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.silverhetch.clotho.Source
import okhttp3.*
import java.lang.IllegalStateException

/**
 * Source of translated string.
 */
class Translation(
    private val auth: Source<String>,
    private val input: Array<String>,
    private val to: String
) : Source<Array<String>> {
    override fun value(): Array<String> {
        val body = JsonArray(input.size).apply {
            input.forEach {
                this.add(JsonObject().apply { addProperty("Text", it) })
            }
        }.toString()
        OkHttpClient().newCall(
            Request.Builder()
                .url(
                    Uri.parse("https://api.cognitive.microsofttranslator.com/translate")
                        .buildUpon()
                        .appendQueryParameter("api-version", "3.0")
                        .appendQueryParameter("to", to)
                        .build().toString()
                ).post(
                    RequestBody.create(
                        MediaType.parse("application/json"),
                        body
                    )
                ).headers(
                    Headers.of(
                        mapOf(
                            Pair(
                                "Authorization",
                                "Bearer ${auth.value()}"
                            )
                        )
                    )
                ).build()
        ).execute().use {
            return if (it.code() == 200) {
                val result = ArrayList<String>()
                JsonParser().parse(it.body()?.string() ?: "[]")
                    .asJsonArray.forEach {
                    result.add(
                        it.asJsonObject.getAsJsonArray("translations")[0]
                            .asJsonObject.get(
                            "text"
                        ).asString
                    )
                }
                result.toTypedArray()
            } else {
                throw IllegalStateException("Translate with Azure failed.")
            }
        }
    }
}