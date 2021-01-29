package com.larryhsiao.nyx.core.attachments.file;

import java.io.File;

public class MemoryNyxFiles implements NyxFiles {
    @Override
    public File fileByUri(String uri) {
        try {
            return File.createTempFile("prefix", "");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
