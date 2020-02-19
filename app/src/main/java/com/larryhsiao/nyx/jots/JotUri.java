package com.larryhsiao.nyx.jots;

import com.larryhsiao.nyx.BuildConfig;
import com.silverhetch.clotho.Source;

import java.net.URI;

/**
 * Source to build Jot uri
 */
public class JotUri implements Source<URI> {
    private final String host;
    private final Jot jot;

    public JotUri(String host, Jot jot) {
        this.host = host;
        this.jot = jot;
    }

    public JotUri(Jot jot) { this(BuildConfig.URI_HOST, jot); }

    @Override
    public URI value() {
        return URI.create(host).resolve("/jots/" + jot.id());
    }
}
