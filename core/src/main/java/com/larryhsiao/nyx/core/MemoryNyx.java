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
    @Override
    public Jots jots() {
        return new MemoryJots();
    }

    @Override
    public Attachments attachments() {
        return new MemoryAttachments();
    }

    @Override
    public NyxFiles files() {
        return new MemoryNyxFiles();
    }
}
