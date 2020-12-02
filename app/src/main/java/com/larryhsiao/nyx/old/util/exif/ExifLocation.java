package com.larryhsiao.nyx.old.util.exif;

import android.location.Location;
import androidx.exifinterface.media.ExifInterface;
import com.larryhsiao.clotho.Source;

import static java.lang.Double.MIN_VALUE;

/**
 * Source to build Location.
 */
public class ExifLocation implements Source<Location> {
    private final ExifInterface exif;

    public ExifLocation(ExifInterface exif) {this.exif = exif;}

    @Override
    public Location value() {
        final double[] latLong = exif.getLatLong();
        Location location = new Location("constant");
        location.setLongitude(MIN_VALUE);
        location.setLatitude(MIN_VALUE);
        if (latLong == null) {
            return location;
        }
        location.setLatitude(latLong[0]);
        location.setLongitude(latLong[1]);
        return location;
    }
}
