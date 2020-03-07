package com.larryhsiao.nyx;

import android.location.Address;
import com.silverhetch.clotho.Source;

/**
 * Source to build a location string.
 */
public class LocationString implements Source<String> {
    private final Address address;

    public LocationString(Address address) {
        this.address = address;
    }

    @Override
    public String value() {
        return String.format(
            "%s%s%s",
            address.getLocality() == null ? "" : address.getLocality(),
            address.getLocality() == null ? "" : ", ",
            address.getCountryName() == null ? "" : address.getCountryName()
        );
    }
}
