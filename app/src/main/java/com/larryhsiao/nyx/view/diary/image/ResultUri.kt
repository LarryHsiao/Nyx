package com.larryhsiao.nyx.view.diary.image

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.core.net.toUri
import com.larryhsiao.nyx.media.storage.NewMediaFile
import com.silverhetch.aura.media.BitmapStream
import com.silverhetch.aura.view.images.CRImage
import com.silverhetch.clotho.Source
import com.silverhetch.clotho.file.ToFile
import kotlinx.android.synthetic.main.page_diary.*

/**
 * Uri from Intent returned by Activity result.
 */
class ResultUri(
    private val context: Context,
    private val data: Intent
) : Source<Uri> {
    override fun value(): Uri {
        val uri = data.data
        if (uri != null) {
            return uri
        }

        val extraData = data.extras?.get("data")
        if (extraData is Bitmap) {
            return NewMediaFile(context).value().also {
                ToFile(
                    BitmapStream(extraData).value(),
                    it
                ) { /* leave progress empty */ }.fire()
            }.toUri()
        }

        throw UnsupportedOperationException("""Unsupported image result""")
    }
}