package com.larryhsiao.nyx.old.attachments;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.larryhsiao.clotho.Action;
import io.github.ponnamkarthik.richlinkpreview.MetaData;
import io.github.ponnamkarthik.richlinkpreview.ResponseListener;
import io.github.ponnamkarthik.richlinkpreview.RichPreview;

/**
 * Action to load preview of http uri.
 */
public class HttpPreviewLoading implements Action {
    private final ImageView img;
    private final String uri;

    public HttpPreviewLoading(ImageView img, String uri) {
        this.img = img;
        this.uri = uri;
    }

    @Override
    public void fire() {
        final RichPreview richPreview = new RichPreview(new ResponseListener() {
            @Override
            public void onData(MetaData metaData) {
                Glide.with(img.getContext())
                    .asBitmap()
                    .load(metaData.getImageurl())
                    .into(new CustomTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(
                            @NonNull Bitmap resource,
                            @Nullable Transition<? super Bitmap> transition) {
                            img.setImageBitmap(resource);
                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {

                        }
                    });
            }

            @Override
            public void onError(Exception e) {

            }
        });
        richPreview.getPreview(uri);
    }

}
