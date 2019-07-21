package com.larryhsiao.nyx.view.diary.attachment

import android.content.Intent
import com.schibstedspain.leku.LATITUDE
import com.schibstedspain.leku.LONGITUDE
import com.silverhetch.clotho.geo.GeoUri
import com.silverhetch.clotho.processor.Processor
import java.lang.Exception
import java.net.URI

/**
 * Processor to find geolocation uri from given data [Intent] returned by Leku library.
 */
class LekuGeoProcessor(
    private val result: (uri: URI) -> Unit
) : Processor<Intent> {
    override fun proceed(input: Intent) {
        try {
            if (input.hasExtra(LATITUDE) && input.hasExtra(LONGITUDE)) {
                result(
                    GeoUri(
                        input.getDoubleExtra(LONGITUDE, 0.0),
                        input.getDoubleExtra(LATITUDE, 0.0)
                    ).value()
                )
            }
        } catch (ignore: Exception) {
            // proceeding failed
        }
    }
}