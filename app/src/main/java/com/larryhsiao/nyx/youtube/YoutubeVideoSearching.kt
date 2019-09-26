package com.larryhsiao.nyx.youtube

import android.content.Context
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.services.youtube.YouTube.*
import com.larryhsiao.nyx.R
import com.silverhetch.clotho.Source
import java.util.ArrayList

/**
 * Source to generate [Video] which searched from Youtube with Data API
 */
class YoutubeVideoSearching(
    private val context: Context,
    private val keyWord: String
) :
    Source<List<Video>> {
    private val service = Builder(
        NetHttpTransport.Builder().build(),
        JacksonFactory.getDefaultInstance()
    ) {
        /*No additional things to do, since we don`t required user to long */
    }.setApplicationName(context.getString(R.string.app_name))
        .build()

    override fun value(): List<Video> {
        val result = ArrayList<Video>()
        val response = service.search().list("snippet")
            .setQ(keyWord)
            .setKey(context.getString(R.string.google_api_key))
            .execute()
        response.items?.forEach {
            result.add(YoutubeVideo(it))
        }
        return result
    }
}