package com.larryhsiao.nyx.jot.map

import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem
import com.larryhsiao.nyx.core.jots.Jot
import java.text.SimpleDateFormat
import java.util.*

/**
 * Item on Cluster grouping map.
 */
class JotMapItem(val jot: Jot) : ClusterItem {
    /**
     * Get id of this jot.
     */
    override fun getPosition(): LatLng {
        return LatLng(jot.location()[1], jot.location()[0])
    }

    override fun getTitle(): String {
        return jot.title().takeIf { !it.isEmpty() } ?: jot.content()
    }

    override fun getSnippet(): String {
        return SimpleDateFormat(
            "HH:mm", Locale.US
        ).format(jot.createdTime()) ?: ""
    }
}