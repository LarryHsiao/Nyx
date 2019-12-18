package com.larryhsiao.nyx.binding

import android.view.View
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.squareup.picasso.Picasso

@BindingAdapter("android:image_uri")
fun loadImage(imageVIew: ImageView, uri: String) {
    if (uri.isNotEmpty()){
        Picasso.get()
            .load(uri)
            .into(imageVIew)
    }
}
