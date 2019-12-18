package com.larryhsiao.nyx.diary

import android.net.Uri

/**
 * Phantom object of [Diary]
 */
class PhantomDiary(private val timestamp: Long = -1) : Diary {
    override fun id(): Long {
        return -1
    }

    override fun title(): String {
        return ""
    }

    override fun timestamp(): Long {
        return timestamp
    }

    override fun attachmentUris(): Array<Uri> {
        return arrayOf()
    }

    override fun weatherIconUrl(): String {
        return ""
    }
}