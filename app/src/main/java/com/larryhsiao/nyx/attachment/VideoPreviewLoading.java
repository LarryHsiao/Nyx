package com.larryhsiao.nyx.attachment;

import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.widget.ImageView;
import com.larryhsiao.nyx.R;
import com.larryhsiao.clotho.Action;

import static com.larryhsiao.nyx.NyxApplication.URI_FILE_PROVIDER;

/**
 * Action to load given video uri's preview.
 */
public class VideoPreviewLoading implements Action {
    private final ImageView image;
    private final String uri;

    public VideoPreviewLoading(ImageView image, String uri) {
        this.image = image;
        this.uri = uri;
    }

    @Override
    public void fire() {
        if (uri.startsWith(URI_FILE_PROVIDER) &&
            !new IsLocalExist(image.getContext(), uri).value()) {
            image.setImageResource(R.drawable.ic_syncing);
        } else {
            MediaMetadataRetriever mmr = new MediaMetadataRetriever();
            mmr.setDataSource(image.getContext(), Uri.parse(uri));
            image.setImageBitmap(mmr.getFrameAtTime());
            mmr.release();
        }
    }
}
