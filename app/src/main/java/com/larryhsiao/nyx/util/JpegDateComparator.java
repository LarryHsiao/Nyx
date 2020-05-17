package com.larryhsiao.nyx.util;

import android.content.Context;
import android.net.Uri;
import androidx.exifinterface.media.ExifInterface;
import com.silverhetch.aura.images.exif.ExifAttribute;
import com.silverhetch.aura.images.exif.ExifUnixTimeStamp;
import com.silverhetch.aura.uri.UriMimeType;
import com.silverhetch.clotho.source.ConstSource;

import java.io.IOException;
import java.util.Comparator;

import static androidx.exifinterface.media.ExifInterface.TAG_DATETIME_ORIGINAL;

/**
 * Comparator for file uri which is a JPEG file.
 */
public class JpegDateComparator implements Comparator<Uri> {
    private final Context context;

    public JpegDateComparator(Context context) {
        this.context = context;
    }

    @Override
    public int compare(Uri o1, Uri o2) {
        try {
            String mimeType1 = new UriMimeType(context, o1.toString()).value();
            String mimeType2 = new UriMimeType(context, o2.toString()).value();
            if ("image/jpeg".equals(mimeType1) && "image/jpeg".equals(mimeType2)) {
                return (int) (datetime(o1) - datetime(o2));
            } else {
                return 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    private long datetime(Uri uri) throws IOException {
        return new ExifUnixTimeStamp(
            new ExifAttribute(
                new ConstSource<>(
                    new ExifInterface(context.getContentResolver().openInputStream(uri))
                ), TAG_DATETIME_ORIGINAL
            )
        ).value();
    }
}
