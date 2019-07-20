package com.larryhsiao.nyx.view.diary.attachment

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.silverhetch.aura.view.images.CRImage
import com.silverhetch.clotho.Source
import com.silverhetch.clotho.processor.Processor
import com.silverhetch.clotho.processor.Processors
import kotlinx.android.synthetic.main.page_diary.*

class ResultProcessor(
    private val context: Context,
    private val result: (uri: Uri) -> Unit
) : Processor<Intent> {
    private val processor = Processors(
        UriProcessor(result),
        BitmapProcessor(context) {
            result(Uri.parse(it.toString()))
        },
        LekuGeoProcessor() {
            result(Uri.parse(it.toString()))
        }
    )

    override fun proceed(input: Intent) {
        processor.proceed(input)
    }
}