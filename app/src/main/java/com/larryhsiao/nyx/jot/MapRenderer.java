package com.larryhsiao.nyx.jot;

import android.content.Context;
import android.net.Uri;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.larryhsiao.nyx.core.attachments.Attachment;
import com.larryhsiao.nyx.core.attachments.AttachmentsByJotId;
import com.larryhsiao.nyx.core.attachments.QueriedAttachments;
import com.silverhetch.aura.view.bitmap.ResizedImage;
import com.silverhetch.aura.view.measures.DP;
import com.silverhetch.clotho.Source;
import com.silverhetch.clotho.source.ConstSource;

import java.sql.Connection;
import java.util.List;

import static android.graphics.BitmapFactory.decodeStream;
import static com.google.android.gms.maps.model.BitmapDescriptorFactory.fromBitmap;
import static java.util.stream.Collectors.toList;

/**
 * Custom renderer for Jot map.
 */
public class MapRenderer extends DefaultClusterRenderer<JotMapItem> {
    private final Context context;
    private final Source<Connection> db;
    private int width = 0;

    public MapRenderer(
        Context context,
        GoogleMap map,
        ClusterManager<JotMapItem> clusterManager,
        Source<Connection> db
    ) {
        super(context, map, clusterManager);
        this.context = context;
        this.db = db;
    }

    @Override
    protected void onBeforeClusterItemRendered(
        JotMapItem item,
        MarkerOptions options
    ) {
        if (width == 0) {
            width = ((int) new DP(context, 100).px());
        }
        List<Attachment> content = new QueriedAttachments(
            new AttachmentsByJotId(db, item.getJot().id())
        ).value()
            .stream()
            .filter(attachment -> attachment.uri().startsWith("content"))
            .limit(1)
            .collect(toList());
        if (content.size() > 0) {
            try {
                options.icon(fromBitmap(new ResizedImage(
                    new ConstSource<>(
                        decodeStream(
                            context.getContentResolver().openInputStream(
                                Uri.parse(content.get(0).uri())
                            )
                        )
                    ), width
                ).value()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        super.onBeforeClusterItemRendered(item, options);
    }
}
