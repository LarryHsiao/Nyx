package com.larryhsiao.nyx.web;

import android.content.Context;
import com.larryhsiao.nyx.core.web.ResourceFiles;

import java.io.IOException;
import java.io.InputStream;

/**
 * Resources files from Android Platform.
 */
public class AndroidResourceFiles implements ResourceFiles {
    private final Context context;

    public AndroidResourceFiles(Context context) {
        this.context = context;
    }

    @Override
    public InputStream open(String path) throws IOException {
        return context.getAssets().open(path);
    }
}
