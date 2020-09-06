package com.larryhsiao.nyx.old.attachments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import com.larryhsiao.nyx.R;
import com.silverhetch.aura.uri.UriMimeType;
import com.silverhetch.clotho.Action;
import com.stfalcon.imageviewer.StfalconImageViewer;

import java.util.Collections;

import static android.content.Intent.ACTION_VIEW;
import static android.widget.Toast.LENGTH_SHORT;
import static android.widget.Toast.makeText;

/**
 * Action to launch a attachment.
 */
public class LaunchAttachment implements Action {
    private final Context context;
    private final String uri;

    public LaunchAttachment(Context context, String uri) {
        this.context = context;
        this.uri = uri;
    }

    @Override
    public void fire() {
        if (uri.startsWith("http")) {
            context.startActivity(new Intent(ACTION_VIEW, Uri.parse(uri)));
        }
        final String mimeType = new UriMimeType(
            context,
            uri
        ).value();
        if (mimeType.startsWith("image")) {
            browseImage(uri);
        } else if (mimeType.startsWith("audio")) {
            browseAudio(uri);
        } else if (mimeType.startsWith("video")) {
            browseVideo(uri);
        }
    }

    private void browseImage(String uri) {
        boolean isLocalExist = new IsLocalExist(context, uri).value();
        if (isLocalExist) {
            new StfalconImageViewer.Builder<>(
                context,
                Collections.singletonList(uri),
                (imageView, image) ->
                    new JotImageLoading(imageView, image).fire()
            ).show();
        } else {
            makeText(
                context,
                R.string.File_no_yet_synced,
                LENGTH_SHORT
            ).show();
        }
    }

    private void browseAudio(String uri) {
        boolean isLocalExist = new IsLocalExist(context, uri).value();
        if (isLocalExist) {
            final Intent intent = new Intent(ACTION_VIEW);
            intent.setDataAndType(Uri.parse(uri), "audio/*");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            context.startActivity(intent);
        } else {
            makeText(
                context,
                R.string.File_no_yet_synced,
                LENGTH_SHORT
            ).show();
        }
    }

    private void browseVideo(String uri) {
        boolean isLocalExist = new IsLocalExist(context, uri).value();
        if (isLocalExist) {
            final Intent intent = new Intent(ACTION_VIEW);
            intent.setDataAndType(Uri.parse(uri), "video/*");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            context.startActivity(intent);
        } else {
            makeText(context, R.string.File_no_yet_synced,
                LENGTH_SHORT).show();
        }
    }
}
