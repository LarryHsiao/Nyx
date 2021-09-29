package com.larryhsiao.nyx.core.attachments;

import java.io.File;
import java.net.URI;

public class FileAttachments implements NyxFiles {
    private final File attachmentRoot;

    public FileAttachments(File attachmentRoot) {
        this.attachmentRoot = attachmentRoot;
    }

    @Override
    public File fileByUri(String uri) {
        try {
            return new File(
                attachmentRoot,
                new URI(uri).getPath()
            );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
