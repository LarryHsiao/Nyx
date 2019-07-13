package com.larryhsiao.nyx

import android.graphics.drawable.Drawable
import androidx.annotation.ColorInt
import androidx.core.graphics.drawable.DrawableCompat
import com.silverhetch.clotho.Source

/**
 * Source to generate drawable that tint with given color.
 *
 * @todo #aura Use Aura version of TintDrawable.
 */
@Deprecated("Use Aura version of this class")
class TintDrawable(
    private val drawable: Drawable,
    @ColorInt private val color: Int
) :
    Source<Drawable> {
    override fun value(): Drawable {
        return DrawableCompat.wrap(drawable).mutate().apply { setTint(color) }
    }
}