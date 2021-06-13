package com.larryhsiao.nyx.attachment;

import android.content.Context;
import com.larryhsiao.clotho.Source;

import java.io.File;

/**
 * Source to build a temp file for jot.
 */
public class TempAttachmentFile implements Source<File> {
    private final Context context;
    private final String fileName;

    public TempAttachmentFile(Context context, String fileName) {
        this.context = context;
        this.fileName = fileName;
    }

    @Override
    public File value() {
        File tempDir = new File(context.getFilesDir(), "attachments_temp");
        tempDir.mkdirs();
        return new File(tempDir, fileName);
    }
}
