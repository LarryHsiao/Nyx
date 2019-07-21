package com.larryhsiao.nyx.view.diary.attachment

import android.content.Context
import android.graphics.drawable.Drawable
import android.net.Uri
import com.silverhetch.aura.view.images.MnemeItem
import com.larryhsiao.nyx.R

/**
 * Represent a Youtube video item.
 */
class YoutubeImage(
    private val context: Context,
    private val id: Uri
) : MnemeItem {
    override fun icon(): Drawable {
        return context.resources.getDrawable(R.drawable.ic_youtube_png)
    }

    override fun id(): String {
        return id.toString()
    }
}