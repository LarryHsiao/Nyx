package com.larryhsiao.nyx.core.youtube;

import com.silverhetch.clotho.Source;

/**
 * Source to build Url for YouTube preview image.
 */
public class YoutubePreviewUrl implements Source<String> {
    private final Source<String> youtubeId;

    public YoutubePreviewUrl(Source<String> youtubeId) {
        this.youtubeId = youtubeId;
    }

    @Override
    public String value() {
        return "https://img.youtube.com/vi/"
            + youtubeId.value()
            + "/hqdefault.jpg";
    }
}
