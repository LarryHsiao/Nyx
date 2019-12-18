package com.larryhsiao.nyx.binding

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.squareup.picasso.Picasso

/**
 * Adapter for loading image from uri
 */
@BindingAdapter("android:image_uri")
fun loadImage(imageVIew: ImageView, uri: String) {
    if (uri.isNotEmpty()) {
        Picasso.get()
            .load(uri)
            .into(imageVIew)
    }
}
