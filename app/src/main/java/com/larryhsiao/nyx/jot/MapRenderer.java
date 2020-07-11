package com.larryhsiao.nyx.jot;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Matrix;
import android.net.Uri;
import androidx.annotation.Nullable;
import androidx.exifinterface.media.ExifInterface;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.larryhsiao.nyx.core.attachments.Attachment;
import com.larryhsiao.nyx.core.attachments.AttachmentsByJotId;
import com.larryhsiao.nyx.core.attachments.QueriedAttachments;
import com.silverhetch.aura.view.measures.DP;
import com.silverhetch.clotho.Source;

import java.io.IOException;
import java.sql.Connection;
import java.util.List;

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
    protected boolean shouldRenderAsCluster(Cluster<JotMapItem> cluster) {
        return cluster.getSize() > 1;
    }

    @Override
    protected void onBeforeClusterItemRendered(JotMapItem item, MarkerOptions options) {
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
                Bitmap bitmap = iconBitmap(Uri.parse(content.get(0).uri()));
                if (bitmap != null) {
                    options.icon(fromBitmap(bitmap));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        super.onBeforeClusterItemRendered(item, options);
    }

    @Nullable
    private Bitmap iconBitmap(Uri uri) throws Exception {
        Options decodeOptions = new Options();
        decodeOptions.inSampleSize = loadSampleSize(uri);
        Bitmap bitmap = BitmapFactory.decodeStream(
            context.getContentResolver().openInputStream(uri),
            null,
            decodeOptions
        );
        ExifInterface exif = new ExifInterface(context.getContentResolver().openInputStream(uri));
        final int rotationDegrees = exif.getRotationDegrees();
        if (rotationDegrees == 0 || bitmap == null) {
            return bitmap;
        } else {
            Matrix matrix = new Matrix();
            matrix.postRotate(rotationDegrees);
            Bitmap rotated = Bitmap.createBitmap(
                bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true
            );
            bitmap.recycle();
            return rotated;
        }
    }

    private int loadSampleSize(Uri uri) throws IOException {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(
            context.getContentResolver().openInputStream(uri),
            null,
            options
        );
        return (int) (options.outWidth / (width * 0.8f));
    }
}
