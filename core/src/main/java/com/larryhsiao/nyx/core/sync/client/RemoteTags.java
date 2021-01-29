package com.larryhsiao.nyx.core.sync.client;

import com.larryhsiao.nyx.core.tags.Tags;

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
}
