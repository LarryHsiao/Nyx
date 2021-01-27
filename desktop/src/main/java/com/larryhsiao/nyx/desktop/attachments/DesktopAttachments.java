package com.larryhsiao.nyx.desktop.attachments;

import com.larryhsiao.nyx.core.attachments.file.NyxFiles;

import java.io.File;

public class DesktopAttachments implements NyxFiles {
    private final File attachmentRoot;

    public DesktopAttachments(File attachmentRoot) {
        this.attachmentRoot = attachmentRoot;
    }

    @Override
    public File fileByUri(String uri) {
        return null;
    }
}
