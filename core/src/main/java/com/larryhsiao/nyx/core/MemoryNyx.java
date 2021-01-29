package com.larryhsiao.nyx.core;

import com.larryhsiao.nyx.core.attachments.Attachments;
import com.larryhsiao.nyx.core.attachments.MemoryAttachments;
import com.larryhsiao.nyx.core.attachments.file.NyxFiles;
import com.larryhsiao.nyx.core.attachments.file.MemoryNyxFiles;
import com.larryhsiao.nyx.core.jots.Jots;
import com.larryhsiao.nyx.core.jots.MemoryJots;

/**
 * Nyx implementation.
 */
public class MemoryNyx implements Nyx {
    private final Jots jots = new MemoryJots();
    private final Attachments attachments = new MemoryAttachments();
    private final NyxFiles files = new MemoryNyxFiles();

    @Override
    public Jots jots() {
        return jots;
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
