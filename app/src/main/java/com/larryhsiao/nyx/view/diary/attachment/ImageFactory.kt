package com.larryhsiao.nyx.view.diary.attachment

import android.content.Context
import android.net.Uri
import com.silverhetch.aura.view.images.CRImage
import com.silverhetch.aura.view.images.MnemeItem
import com.silverhetch.clotho.Source

/**
 * Source to generate [MnemeItem] from given uri
 */
class ImageFactory(
    private val context: Context,
    private val uri: Uri
) :
    Source<MnemeItem> {
    override fun value(): MnemeItem {
        if (uri.toString().startsWith("geo:")) {
            return GeoImage(context, uri)
        }

        return CRImage(context, uri)
    }
}