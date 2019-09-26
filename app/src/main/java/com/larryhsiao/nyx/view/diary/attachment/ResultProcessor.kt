package com.larryhsiao.nyx.view.diary.attachment

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.silverhetch.clotho.processor.Processor
import com.silverhetch.clotho.processor.Processors

/**
 * Processor to extract the uri from result of attachment Intent
 *
 * @todo #10 The result might have multiple output, use the proper one(e.g. Camera capture)
 *
 * @see FindAttachmentIntent
 */
class ResultProcessor(
    private val context: Context,
    private val result: (uri: Uri) -> Unit
) : Processor<Intent> {
    private val processor = Processors(
        UriProcessor(result),
        BitmapProcessor(context) {
            result(Uri.parse(it.toString()))
        },
        LekuGeoProcessor {
            result(Uri.parse(it.toString()))
        }
    )

    override fun proceed(input: Intent) {
        processor.proceed(input)
    }
}