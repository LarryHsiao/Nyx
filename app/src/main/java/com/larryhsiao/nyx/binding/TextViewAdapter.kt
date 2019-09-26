package com.larryhsiao.nyx.binding

import android.widget.TextView
import androidx.databinding.BindingAdapter
import java.text.DateFormat
import java.util.*

/**
 * Format timestamp with default locale setting
 */
@BindingAdapter("android:datetime")
fun formatDateTime(textView: TextView, millis: Long) {
    if (millis != 0L) {
        textView.text = DateFormat.getDateInstance()
            .format(Date().also { it.time = millis })
    }
}
