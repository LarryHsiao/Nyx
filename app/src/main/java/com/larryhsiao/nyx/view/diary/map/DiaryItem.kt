package com.larryhsiao.nyx.view.diary.map

import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem
import com.larryhsiao.nyx.diary.Diary

/**
 * Implementation of diary item for cluster map drawing.
 */
class DiaryItem(
    private val diary: Diary
) : ClusterItem {
    private val location: LatLng by lazy {
        var result = LatLng(0.0, 0.0)

        diary.attachmentUris().forEach {
            if (it.toString().startsWith("geo:")) {
                val segment = it.toString()
                    .replace("geo:", "")
                    .split(",")
                result = LatLng(
                    segment[0].toDouble(),
                    segment[1].toDouble()
                )
            }
        }
        result
    }

    /**
     * The [Diary] this object represents.
     */
    fun diary(): Diary {
        return diary
    }

    override fun getSnippet(): String {
        return "snippet"
    }

    override fun getTitle(): String {
        return diary.title()
    }

    override fun getPosition(): LatLng {
        return location
    }
}