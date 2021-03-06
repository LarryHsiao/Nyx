package com.larryhsiao.nyx.core;

import com.larryhsiao.clotho.Source;
import com.larryhsiao.nyx.core.attachments.Attachments;
import com.larryhsiao.nyx.core.attachments.LocalAttachments;
import com.larryhsiao.nyx.core.attachments.NyxFiles;
import com.larryhsiao.nyx.core.jots.Jots;
import com.larryhsiao.nyx.core.jots.LocalJots;
import com.larryhsiao.nyx.core.metadata.LocalMetadataSet;
import com.larryhsiao.nyx.core.metadata.MetadataSet;
import com.larryhsiao.nyx.core.tags.JotTags;
import com.larryhsiao.nyx.core.tags.LocalJotTags;
import com.larryhsiao.nyx.core.tags.LocalTags;
import com.larryhsiao.nyx.core.tags.Tags;

import java.sql.Connection;

/**
 * Implementation of local storing data.
 */
public class LocalNyx implements Nyx {
    private final Source<Connection> db;
    private final NyxFiles nyxFiles;

    public LocalNyx(Source<Connection> db, NyxFiles nyxFiles) {
        this.db = db;
        this.nyxFiles = nyxFiles;
    }

    @Override
    public Jots jots() {
        return new LocalJots(db);
    }

    @Override
    public JotTags jotTags() {
        return new LocalJotTags(db);
    }

    @Override
    public Tags tags() {
        return new LocalTags(db);
    }

    @Override
    public MetadataSet metadataSet() {
        return new LocalMetadataSet(db);
    }

    @Override
    public Attachments attachments() {
        return new LocalAttachments(db);
    }

    @Override
    public NyxFiles files() {
        return nyxFiles;
    }
}
