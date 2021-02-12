package com.larryhsiao.nyx.core.sync.client;

import com.larryhsiao.nyx.core.sync.client.endpoints.tags.GetJotTags;
import com.larryhsiao.nyx.core.sync.client.endpoints.tags.PostJotTags;
import com.larryhsiao.nyx.core.tags.AllJotTags;
import com.larryhsiao.nyx.core.tags.ConstJotTag;
import com.larryhsiao.nyx.core.tags.JotTag;
import com.larryhsiao.nyx.core.tags.JotTags;

import java.util.List;

/**
 * Remote implementation of {@link JotTags}.
 */
public class RemoteJotTags implements JotTags {
    private final String host;

    public RemoteJotTags(String host) {
        this.host = host;
    }

    @Override
    public List<JotTag> all() {
        return new GetJotTags(host).value();
    }

    @Override
    public void link(long jotId, long tagId) {
        new PostJotTags(host, new ConstJotTag(
            jotId, tagId, false, 0
        )).fire();
    }

    @Override
    public void update(JotTag jotTag) {

    }

    @Override
    public void deleteByIds(long jotId, long tagId) {

    }
}
