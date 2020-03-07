package com.larryhsiao.nyx.core.jots;

import com.silverhetch.clotho.Source;

import java.net.URI;

/**
 * Source to build Jot from uri.
 */
public class JotUriId implements Source<Long> {
    private final String uri;

    public JotUriId(String uri) {
        this.uri = uri;
    }

    @Override
    public Long value() {
        URI uriObj = URI.create(uri);
        if (uriObj.getPath().startsWith("/jots/")) {
            return Long.valueOf(uriObj.getPath().replace("/jots/", ""));
        } else {
            throw new IllegalArgumentException("Not a jot uri");
        }
    }
}
