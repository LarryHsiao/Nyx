package com.larryhsiao.nyx.view.diary.attachment

import android.content.Context
import android.content.Intent
import android.provider.MediaStore
import com.larryhsiao.nyx.R
import com.schibstedspain.leku.LocationPickerActivity
import com.silverhetch.aura.intent.ChooserIntent
import com.silverhetch.clotho.Source

/**
 * Source to generate Intent for finding Image.
 */
class FindAttachmentIntent(private val context: Context) : Source<Intent> {
    override fun value(): Intent {
        return ChooserIntent(
            context,
            context.getString(R.string.add_image),
            Intent(Intent.ACTION_GET_CONTENT).also { it.type = "image/*" },
            Intent(MediaStore.ACTION_IMAGE_CAPTURE),
            LocationPickerActivity.Builder()
                .shouldReturnOkOnBackPressed()
                .withStreetHidden()
                .withCityHidden()
                .withZipCodeHidden()
                .withSatelliteViewHidden()
                .build(context)
        ).value()
    }
}
