package com.larryhsiao.nyx.view.diary.attachment

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.silverhetch.aura.view.images.ImageActivity
import com.silverhetch.clotho.Source

/**
 * Source which builds [Intent] for viewing Uri content.
 */
class ViewAttachementIntent(
    private val context: Context,
    private val uri: Uri
) : Source<Intent> {
    override fun value(): Intent {
        return if (uri.toString().startsWith("geo")) {
            Intent(Intent.ACTION_VIEW, uri)
        } else {
            ImageActivity.newIntent(context, uri.toString())
        }
    }
}