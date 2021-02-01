package com.larryhsiao.nyx.core.sync.client.endpoints.tags;

import com.larryhsiao.clotho.Action;
import com.larryhsiao.clotho.io.StringOutput;
import com.larryhsiao.nyx.core.jots.Jot;
import com.larryhsiao.nyx.core.sync.json.jots.JotJson;
import com.larryhsiao.nyx.core.sync.json.tags.TagJson;
import com.larryhsiao.nyx.core.tags.Tag;

import java.net.HttpURLConnection;
import java.net.URL;

import static com.larryhsiao.nyx.core.sync.server.NyxServer.ENDPOINT_JOTS;
import static com.larryhsiao.nyx.core.sync.server.NyxServer.ENDPOINT_TAGS;

/**
 * Update a exist jot create new one if not exist.
 * Note that there are no new id in response for further tracking the jot.
 */
public class PostTag implements Action {
    private final String host;
    private final Tag tag;

    public PostTag(String host, Tag tag) {
        this.host = host;
        this.tag = tag;
    }

    @Override
    public void fire() {
        try {
            HttpURLConnection conn = (HttpURLConnection) new URL(
                host + ENDPOINT_TAGS
            ).openConnection();
            conn.setRequestMethod("POST");
            conn.addRequestProperty("Content-Type", "application/json");
            new StringOutput(
                new TagJson(tag).value().toString(),
                conn.getOutputStream()
            ).fire();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
