package com.larryhsiao.nyx.old.sync.encryption.glide;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.ModelLoader;
import com.larryhsiao.nyx.old.settings.NyxSettings;

import java.io.InputStream;

/**
 * ModelLoader for loading encrypted images
 */
public class EncryptionModelLoader implements ModelLoader<GlideUrl, InputStream> {
    private final NyxSettings settings;

    public EncryptionModelLoader(NyxSettings settings) {
        this.settings = settings;
    }

    @Nullable
    @Override
    public LoadData<InputStream> buildLoadData(
        @NonNull GlideUrl model, int width, int height, @NonNull Options options) {
        return new LoadData<>(model, new EncryptedDataFetcher(settings, model));
    }

    @Override
    public boolean handles(@NonNull GlideUrl s) {
        return s.toStringUrl().startsWith("https://firebasestorage.googleapis.com");
    }
}
