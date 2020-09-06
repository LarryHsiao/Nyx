package com.larryhsiao.nyx.old.attachments;

import android.content.Context;
import android.net.Uri;
import com.silverhetch.clotho.Source;

import java.io.File;
import java.util.Objects;

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
        try {
            if (uri.startsWith(URI_FILE_PROVIDER)) {
                return new File(
                    new File(
                        context.getFilesDir(),
                        "attachments"
                    ),
                    uri.replace(URI_FILE_PROVIDER, "")
                ).exists();
            } else if (uri.startsWith("content:")) {
                Objects.requireNonNull(context.getContentResolver()
                    .openInputStream(Uri.parse(uri))).close();
                return true;
            } else {
                return false;
            }
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }
}
