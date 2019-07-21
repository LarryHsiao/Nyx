package com.larryhsiao.nyx.youtube

import com.google.api.services.youtube.model.SearchResult

/**
 * Represent a Youtube Video
 */
class YoutubeVideo(private val result: SearchResult) : Video {
    override fun id(): String {
        return "http://www.youtube.com/watch?v=${result.id.videoId}"
    }

    override fun title(): String {
        return result.snippet.title
    }

    override fun thumbnailUrl(): String {
        return result.snippet.thumbnails.default.url
    }
}