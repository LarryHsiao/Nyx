package com.larryhsiao.nyx.attachments;

import android.content.Context;
import com.silverhetch.clotho.Source;

import java.io.File;

import static com.larryhsiao.nyx.JotApplication.URI_FILE_PROVIDER;

/**
 * Source to determine if the given Attachment uri exist.
 */
public class IsLocalExist implements Source<Boolean> {
    private final Context context;
    private final String uri;

    public IsLocalExist(Context context, String uri) {
        this.context = context;
        this.uri = uri;
    }

    @Override
    public Boolean value() {
        if (!uri.startsWith(URI_FILE_PROVIDER)){
            throw new RuntimeException("Not a Jot Uri");
        }
        return new File(
                new File(
                    context.getFilesDir(),
                    "attachments"
                ),
                uri.replace(URI_FILE_PROVIDER, "")
            ).exists();
    }
}
