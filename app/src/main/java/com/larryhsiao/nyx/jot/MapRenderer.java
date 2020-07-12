package com.larryhsiao.nyx.jot;

import android.content.Context;
import android.graphics.*;
import android.graphics.BitmapFactory.Options;
import android.net.Uri;
import androidx.annotation.Nullable;
import androidx.exifinterface.media.ExifInterface;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
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

import static android.graphics.Bitmap.Config.ARGB_8888;
import static android.graphics.Color.WHITE;
import static android.graphics.PorterDuff.Mode.SRC_OVER;
import static com.google.android.gms.maps.model.BitmapDescriptorFactory.fromBitmap;
import static com.larryhsiao.nyx.JotApplication.URI_FILE_PROVIDER;
import static java.util.stream.Collectors.toList;

/**
 * Custom renderer for Jot map.
 */
public class MapRenderer extends DefaultClusterRenderer<JotMapItem> {
    private final Context context;
    private final Source<Connection> db;
    private int width = 0;
    private int padding4 = 0;

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
    protected void onBeforeClusterRendered(Cluster<JotMapItem> cluster, MarkerOptions options) {
        generateDimensions();
        if (cluster.getSize() > 0) {
            for (JotMapItem item : cluster.getItems()) {
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
                            options.icon(clusterIcon(cluster, bitmap));
                        }
                        break;
                    } catch (Exception e) {
                        super.onBeforeClusterRendered(cluster, options);
                    }
                }
            }
        } else {
            super.onBeforeClusterRendered(cluster, options);
        }
    }

    private BitmapDescriptor clusterIcon(Cluster<JotMapItem> cluster, Bitmap bitmap) {
        if (!bitmap.isMutable()) {
            Bitmap tempBitmap = bitmap.copy(ARGB_8888, true);
            bitmap.recycle();
            bitmap = tempBitmap;
        }
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        paint.setTextSize(35);
        paint.setXfermode(new PorterDuffXfermode(SRC_OVER));
        String indicator = String.valueOf(cluster.getItems().size());
        Rect bounds = new Rect();
        paint.getTextBounds(indicator, 0, indicator.length(), bounds);
        paint.setColor(Color.parseColor("#BB000000"));
        canvas.drawRoundRect(
            bitmap.getWidth() - bounds.width() - (padding4 *2),
            bitmap.getHeight() - bounds.height()- (padding4 *2),
            bitmap.getWidth() ,
            bitmap.getHeight(),
            padding4,
            padding4,
            paint
        );
        paint.setColor(WHITE);
        canvas.drawText(
            indicator,
            bitmap.getWidth() - bounds.width() - padding4,
            bitmap.getHeight() - padding4,
            paint
        );
        return fromBitmap(bitmap);
    }

    @Override
    protected void onBeforeClusterItemRendered(JotMapItem item, MarkerOptions options) {
        generateDimensions();
        List<Attachment> content = new QueriedAttachments(
            new AttachmentsByJotId(db, item.getJot().id())
        ).value()
            .stream()
            .filter(attachment -> attachment.uri().startsWith(URI_FILE_PROVIDER))
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

    private void generateDimensions() {
        if (width == 0) {
            width = ((int) new DP(context, 100).px());
        }
        if (padding4 == 0) {
            padding4 = ((int) new DP(context, 4).px());
        }
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
