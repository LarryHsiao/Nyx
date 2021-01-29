package com.larryhsiao.nyx.core.sync.client;

import com.larryhsiao.nyx.core.sync.client.endpoints.tags.DeleteTag;
import com.larryhsiao.nyx.core.sync.client.endpoints.tags.GetTags;
import com.larryhsiao.nyx.core.sync.client.endpoints.tags.PostTag;
import com.larryhsiao.nyx.core.sync.client.endpoints.tags.PutTag;
import com.larryhsiao.nyx.core.tags.Tag;
import com.larryhsiao.nyx.core.tags.Tags;

import java.util.List;

/**
 * Tags from remote server vis http.
 *
 * @todo #112-3 Remote implementation of {@link Tags}.
 */
public class RemoteTags implements Tags {
    private final String host;

    public RemoteTags(String host) {
        this.host = host;
    }

    @Override
    public List<Tag> all() {
        return new GetTags(host).value();
    }

    @Override
    public Tag create(Tag tag) {
        return new PutTag(host, tag).value();
    }

    @Override
    public void update(Tag tag) {
        new PostTag(host, tag).fire();
    }

    @Override
    public void deleteById(long id) {
        new DeleteTag(host, id).fire();
    }
}
