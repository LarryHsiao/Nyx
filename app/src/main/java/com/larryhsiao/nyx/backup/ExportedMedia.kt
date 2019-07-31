package com.larryhsiao.nyx.backup

import android.net.Uri

/**
 * Object of Exported media in general format
 */
interface ExportedMedia {
    /**
     * The json object of the media row.
     */
    fun json(): String

    /**
     * Media file uri.
     */
    fun mediaUri(): Uri

    /**
     * @return true if the media uri is file.
     */
    fun isFile(): Boolean
}