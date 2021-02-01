package com.larryhsiao.nyx.core.sync.client.endpoints.tags;

import com.larryhsiao.clotho.Source;
import com.larryhsiao.clotho.io.StringOutput;
import com.larryhsiao.nyx.core.sync.json.tags.TagJson;
import com.larryhsiao.nyx.core.tags.Tag;
import com.larryhsiao.nyx.core.tags.WrappedTag;

import javax.json.Json;
import java.net.HttpURLConnection;
import java.net.URL;

import static com.larryhsiao.nyx.core.sync.server.NyxServer.ENDPOINT_JOTS;
import static com.larryhsiao.nyx.core.sync.server.NyxServer.ENDPOINT_TAGS;

/**
 * Source to create new {@link Tag} to server.
 * <p>
 * Will return a {@link Tag} with new id.
 */
public class PutTag implements Source<Tag> {
    private final String host;
    private final Tag tag;

    public PutTag(String host, Tag tag) {
        this.host = host;
        this.tag = tag;
    }

    @Override
    public Tag value() {
        try {
            HttpURLConnection conn = (HttpURLConnection) new URL(
                host + ENDPOINT_TAGS
            ).openConnection();
            conn.setRequestMethod("PUT");
            conn.addRequestProperty("Content-Type", "application/json");
            new StringOutput(
                new TagJson(tag).value().toString(),
                conn.getOutputStream()
            ).fire();
            final long id = Json.createReader(
                conn.getInputStream()
            ).readObject().getJsonNumber("id").longValue();
            return new WrappedTag(tag) {
                @Override
                public long id() {
                    return id;
                }
            };
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
