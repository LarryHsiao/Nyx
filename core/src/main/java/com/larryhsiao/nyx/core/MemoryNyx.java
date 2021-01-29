package com.larryhsiao.nyx.core;

import com.larryhsiao.nyx.core.attachments.Attachments;
import com.larryhsiao.nyx.core.attachments.MemoryAttachments;
import com.larryhsiao.nyx.core.attachments.file.NyxFiles;
import com.larryhsiao.nyx.core.attachments.file.MemoryNyxFiles;
import com.larryhsiao.nyx.core.jots.Jots;
import com.larryhsiao.nyx.core.jots.MemoryJots;
import com.larryhsiao.nyx.core.metadata.MetadataSet;
import com.larryhsiao.nyx.core.tags.MemoryTags;
import com.larryhsiao.nyx.core.tags.Tags;

/**
 * Nyx implementation.
 */
public class MemoryNyx implements Nyx {
    private final Jots jots = new MemoryJots();
    private final Attachments attachments = new MemoryAttachments();
    private final NyxFiles files = new MemoryNyxFiles();
    private final Tags tags = new MemoryTags();

    @Override
    public Jots jots() {
        return jots;
    }

    @Override
    public Tags tags() {
        return tags;
    }

    @Override
    public MetadataSet metadataSet() {
        return null;
    }

    @Override
    public Attachments attachments() {
        return attachments;
    }

    @Override
    public NyxFiles files() {
        return files;
    }
}
