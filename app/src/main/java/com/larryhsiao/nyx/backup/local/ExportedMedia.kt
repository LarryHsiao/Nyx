package com.larryhsiao.nyx.backup.local

import androidx.annotation.Keep
import com.larryhsiao.nyx.media.room.MediaEntity

/**
 * Object of exported media
 */
@Keep
data class ExportedMedia(
    val media: MediaEntity,
    val exportedFileName: String
)