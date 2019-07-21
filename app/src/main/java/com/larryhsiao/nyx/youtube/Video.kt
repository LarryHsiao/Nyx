package com.larryhsiao.nyx.youtube

/**
 * Represent a video
 */
interface Video {
    /**
     * Id of the video
     */
    fun id(): String

    /**
     * The title of the video
     */
    fun title(): String

    /**
     * Thumbnail uri of the video.
     */
    fun thumbnailUrl(): String
}