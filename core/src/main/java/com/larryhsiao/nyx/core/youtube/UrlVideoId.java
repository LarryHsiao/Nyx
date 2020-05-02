package com.larryhsiao.nyx.core.youtube;

import com.silverhetch.clotho.Source;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;

/**
 * Source to build a video id from given url.
 */
public class UrlVideoId implements Source<String> {
    private final String url;

    public UrlVideoId(String url) {
        this.url = url;
    }

    @Override
    public String value() {
        try {
            URL urlObj = new URL(url);
            if ("youtu.be".equals(urlObj.getHost())) {
                return urlObj.getPath().substring(1);
            }

            if ("www.youtube.com".equals(urlObj.getHost())) {
                String query = urlObj.getQuery();
                String[] pairs = query.split("&");
                for (String pair : pairs) {
                    int idx = pair.indexOf("=");
                    String key = URLDecoder.decode(pair.substring(0, idx), "UTF-8");
                    if ("v".equals(key)){
                        return URLDecoder.decode(pair.substring(idx+1), "UTF-8");
                    }
                }
            }
            return "";
        } catch (MalformedURLException | UnsupportedEncodingException e) {
            e.printStackTrace();
            return "";
        }
    }
}
