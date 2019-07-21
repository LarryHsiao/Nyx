package com.larryhsiao.nyx.view.diary.attachment

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import androidx.core.net.toUri
import com.larryhsiao.nyx.media.storage.NewMediaFile
import com.silverhetch.aura.media.BitmapStream
import com.silverhetch.clotho.file.ToFile
import com.silverhetch.clotho.processor.Processor
import java.net.URI

/**
 * Processor to find Bitmap from given [Intent].
 */
class BitmapProcessor(
    private val context: Context,
    private val result: (uri: URI) -> Unit
) : Processor<Intent> {
    override fun proceed(input: Intent) {
        val extraData = input.extras?.get("data")
        if (extraData is Bitmap) {
            result(
                NewMediaFile(context).value().also {
                    ToFile(
                        BitmapStream(extraData).value(),
                        it
                    ) { /* leave progress empty */ }.fire()
                }.toURI()
            )
        }
    }
}