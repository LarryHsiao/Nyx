package com.larryhsiao.nyx.core.youtube;

import com.silverhetch.clotho.Source;

import java.net.URI;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Source to build boolean if the given url is youtube url.
 */
public class IsYoutubeUrl implements Source<Boolean> {
    private static final Set<String> youtubeHosts;

    static {
        youtubeHosts = new HashSet<>();
        youtubeHosts.addAll(
            Arrays.asList(
                "youtu.be",
                "www.youtube.com"
            )
        );
    }

    private final String url;

    public IsYoutubeUrl(String url) {
        this.url = url;
    }

    @Override
    public Boolean value() {
        try {
            URI uri = URI.create(url);
            return youtubeHosts.contains(uri.getHost());
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
