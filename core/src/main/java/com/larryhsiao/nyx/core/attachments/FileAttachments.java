package com.larryhsiao.nyx.core.attachments;

import java.io.File;

public class FileAttachments implements NyxFiles {
    private final File attachmentRoot;

    public FileAttachments(File attachmentRoot) {
        this.attachmentRoot = attachmentRoot;
    }

    @Override
    public File fileByUri(String uri) {
        return null;
    }
}
