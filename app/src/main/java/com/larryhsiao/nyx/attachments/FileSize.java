package com.larryhsiao.nyx.attachments;

import com.silverhetch.clotho.Source;

import java.io.File;

/**
 * Source to generte file size in bytes.
 */
public class FileSize implements Source<Long> {
    private final File file;

    public FileSize(File file) {this.file = file;}

    @Override
    public Long value() {
        return calculate(file);
    }

    private long calculate(File file) {
        if (file.isDirectory()) {
            long result = 0;
            File[] files = file.listFiles();
            if (files == null) {
                return 0;
            }
            for (File listFile : files) {
                result += calculate(listFile);
            }
            return result;
        } else {
            return file.length();
        }
    }
}
