package com.larryhsiao.nyx.old.sync.encryption.glide;

import androidx.annotation.NonNull;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.ModelLoader;
import com.bumptech.glide.load.model.ModelLoaderFactory;
import com.bumptech.glide.load.model.MultiModelLoaderFactory;
import com.larryhsiao.nyx.old.settings.NyxSettings;

import java.io.InputStream;

/**
 * Factory for {@link EncryptionModelLoader}
 */
public class EncryptionModelLoaderFactory implements ModelLoaderFactory<GlideUrl, InputStream> {
    private final NyxSettings settings;

    public EncryptionModelLoaderFactory(NyxSettings settings) {this.settings = settings;}

    @NonNull
    @Override
    public ModelLoader<GlideUrl, InputStream> build(
        @NonNull MultiModelLoaderFactory multiFactory
    ) { return new EncryptionModelLoader(settings); }

    @Override
    public void teardown() {

    }
}
