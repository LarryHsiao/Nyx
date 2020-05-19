package com.larryhsiao.nyx.attachments;

import android.graphics.BlendMode;
import android.graphics.BlendModeColorFilter;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.PorterDuff;
import android.widget.ImageView;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.larryhsiao.nyx.R;
import com.silverhetch.clotho.Action;

import static android.graphics.Color.BLACK;
import static android.graphics.Color.WHITE;
import static android.graphics.PorterDuff.Mode.ADD;
import static android.graphics.PorterDuff.Mode.CLEAR;
import static androidx.swiperefreshlayout.widget.CircularProgressDrawable.LARGE;
import static com.larryhsiao.nyx.JotApplication.URI_FILE_PROVIDER;

/**
 * Action to load up a Image from Jot Uri.
 * It will load remote image at Firestore if the Uri is generated by Jotted and not exist at local .
 */
public class JotImageLoading implements Action {
    private final ImageView image;
    private final String uri;
    private final int progressColor;

    public JotImageLoading(ImageView image, String uri){
        this(image, uri, BLACK);
    }

    public JotImageLoading(ImageView image, String uri, int progressColor) {
        this.image = image;
        this.uri = uri;
        this.progressColor = progressColor;
    }

    @Override
    public void fire() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (uri.startsWith(URI_FILE_PROVIDER) &&
            !new IsLocalExist(image.getContext(), uri).value() &&
            user != null
        ) {
            final CircularProgressDrawable progress = new CircularProgressDrawable(image.getContext());
            progress.setColorSchemeColors(progressColor);
            progress.setStyle(LARGE);
            progress.start();
            image.setImageDrawable(progress);
            FirebaseStorage.getInstance()
                .getReference()
                .child(user.getUid() + "/" + uri.replace(URI_FILE_PROVIDER, ""))
                .getDownloadUrl().addOnSuccessListener(downloadUrl -> {
                Glide.with(image.getContext())
                    .load(downloadUrl)
                    .placeholder(progress)
                    .into(image);
            }).addOnFailureListener(e -> image.setImageResource(R.drawable.ic_missing));
        } else {
            CircularProgressDrawable progress = new CircularProgressDrawable(image.getContext());
            progress.setStyle(LARGE);
            progress.start();
            Glide.with(image.getContext())
                .load(uri)
                .placeholder(progress)
                .into(image);
        }
    }
}
