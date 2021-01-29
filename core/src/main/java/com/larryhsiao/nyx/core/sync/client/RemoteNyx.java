package com.larryhsiao.nyx.core.sync.client;

import com.larryhsiao.nyx.core.Nyx;
import com.larryhsiao.nyx.core.attachments.Attachments;
import com.larryhsiao.nyx.core.attachments.file.NyxFiles;
import com.larryhsiao.nyx.core.jots.Jots;
import com.larryhsiao.nyx.core.metadata.MetadataSet;
import com.larryhsiao.nyx.core.tags.Tags;

// @todo #110 Complete this
/**
 * Remote implementation of {@link Nyx}.
 * Which fetch the Jots from another Nyx application.
 */
public class RemoteNyx implements Nyx {
    private final String host;

    public RemoteNyx(String host) {
        this.host = host;
    }

    @Override
    public Tags tags() {
        return new RemoteTags(host);
    }

    @Override
    public MetadataSet metadataSet() {
        return null;
    }

    @Override
    public Jots jots() {
        return new RemoteJots(host);
    }

    @Override
    public Attachments attachments() {
        return null;
    }

    @Override
    public NyxFiles files() {
        return null;
    }
}
