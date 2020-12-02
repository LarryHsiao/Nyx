package com.larryhsiao.nyx.old.attachments;

import android.widget.ImageView;
import com.larryhsiao.nyx.R;
import com.larryhsiao.clotho.Action;

import static android.widget.ImageView.ScaleType.CENTER_INSIDE;

/**
 * Action to load audio preview image.
 */
public class AudioImageLoading implements Action {
    private final ImageView image;
    private final String url;

    public AudioImageLoading(ImageView image, String url) {
        this.image = image;
        this.url = url;
    }

    @Override
    public void fire() {
        if (new IsLocalExist(image.getContext(), url).value()) {
            image.setImageResource(R.drawable.ic_audio);
        } else {
            image.setImageResource(R.drawable.ic_syncing);
        }
        image.setScaleType(CENTER_INSIDE);
    }
}
