package com.larryhsiao.nyx.view.diary.attachment

import android.content.Context
import android.graphics.drawable.Drawable
import android.net.Uri
import com.larryhsiao.nyx.R
import com.silverhetch.aura.view.images.MnemeItem

/**
 * Implementation of MnemeItem for geolocation.
 */
class GeoImage(
    private val context: Context,
    private val uri: Uri
) : MnemeItem {
    override fun icon(): Drawable {
        return context.resources.getDrawable(R.drawable.ic_location)
    }

    override fun id(): String {
        return uri.toString()
    }
}