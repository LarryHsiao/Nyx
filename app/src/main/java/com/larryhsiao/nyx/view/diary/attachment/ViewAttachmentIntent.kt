package com.larryhsiao.nyx.view.diary.attachment

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.silverhetch.aura.view.images.ImageActivity
import com.silverhetch.clotho.Source

/**
 * Source which builds [Intent] for viewing Uri content.
 */
class ViewAttachmentIntent(
    private val context: Context,
    private val uri: Uri
) : Source<Intent> {
    override fun value(): Intent {
        return if (uri.toString().startsWith("file")) {
            ImageActivity.newIntent(context, uri.toString())
        } else {
            val uriStr = uri.toString()
            if (uriStr.startsWith("geo:")) {
                val segments = uriStr.replace("geo:", "").split(",")
                Intent(
                    Intent.ACTION_VIEW,
                    uri.buildUpon().appendQueryParameter(
                        "q",
                        """${segments[0]},${segments[1]}('')"""
                    ).build()
                )
            } else {
                Intent(Intent.ACTION_VIEW, uri)
            }
        }
    }
}