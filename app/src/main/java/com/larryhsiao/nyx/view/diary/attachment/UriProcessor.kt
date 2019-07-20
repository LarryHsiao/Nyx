package com.larryhsiao.nyx.view.diary.attachment

import android.content.Intent
import android.net.Uri
import com.silverhetch.clotho.processor.Processor

/**
 * Processor to extract uri from given [Intent].
 */
class UriProcessor(private val result:(uri:Uri)->Unit) : Processor<Intent> {
    override fun proceed(input: Intent) {
        input.data?.also {
            result(it)
        }
    }
}