package com.larryhsiao.nyx.utils

import android.graphics.Color
import android.text.Spannable
import android.text.SpannableString
import android.text.style.BackgroundColorSpan
import android.text.style.ForegroundColorSpan
import android.widget.EditText
import android.widget.TextView
import android.widget.TextView.BufferType.SPANNABLE
import com.larryhsiao.nyx.R
import com.larryhsiao.nyx.core.tags.Tag
import com.larryhsiao.clotho.Action

/**
 * Action to do enlighten hash tag in textView.
 */
class HashTagEnlightenAction(
    private val textView: TextView,
    private val content: String,
    private val tags: Map<String, Tag>
) : Action {
    override fun fire() {
        val cursorPosition = textView.selectionStart
        val spannable = SpannableString(content)
        tags.keys.forEach { tagName ->
            try {
                val tagStr = "#$tagName"
                val started = content.indexOf(tagStr)
                spannable.setSpan(
                    BackgroundColorSpan(
                        textView.resources.getColor(R.color.colorPrimary)
                    ),
                    started,
                    started + tagStr.length,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                spannable.setSpan(
                    ForegroundColorSpan(Color.WHITE),
                    started,
                    started + tagStr.length,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        textView.setText(spannable, SPANNABLE)
        if (textView is EditText) {
            textView.setSelection(cursorPosition)
        }
    }
}