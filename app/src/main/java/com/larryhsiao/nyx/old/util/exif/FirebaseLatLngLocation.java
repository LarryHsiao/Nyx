package com.larryhsiao.nyx.old.util.exif;

import android.location.Location;
import com.google.firebase.ml.vision.common.FirebaseVisionLatLng;
import com.silverhetch.clotho.Source;

/**
 * Source to build Location obj from firebase version's Location obj.
 */
public class FirebaseLatLngLocation implements Source<Location> {
    private final FirebaseVisionLatLng latLng;

    public FirebaseLatLngLocation(FirebaseVisionLatLng latLng) {this.latLng = latLng;}

    @Override
    public Location value() {
        Location location = new Location("constant");
        location.setLongitude(latLng.getLongitude());
        location.setLatitude(latLng.getLatitude());
        return location;
    }
}
