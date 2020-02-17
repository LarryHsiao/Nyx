package com.larryhsiao.nyx.android.jot;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;
import com.larryhsiao.nyx.jots.Jot;
import com.silverhetch.clotho.time.HttpTimeFormat;

/**
 * Item on Cluster grouping map.
 */
public class JotMapItem implements ClusterItem {
    private final Jot jot;

    public JotMapItem(Jot jot) {
        this.jot = jot;
    }

    /**
     * Get id of this jot.
     */
    public long getId() {
        return jot.id();
    }

    @Override
    public LatLng getPosition() {
        return new LatLng(jot.location()[1], jot.location()[0]);
    }

    @Override
    public String getTitle() {
        return jot.content();
    }

    @Override
    public String getSnippet() {
        return new HttpTimeFormat().value().format(jot.createdTime());
    }
}
