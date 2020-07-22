package com.larryhsiao.nyx;

import android.content.Context;
import androidx.annotation.NonNull;
import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.Registry;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.module.AppGlideModule;
import com.larryhsiao.nyx.settings.DefaultPreference;
import com.larryhsiao.nyx.settings.NyxSettingsImpl;
import com.larryhsiao.nyx.sync.encryption.glide.EncryptionModelLoaderFactory;

import java.io.InputStream;

/**
 * Standard Glide Model.
 */
@GlideModule
public class NyxGlideModule extends AppGlideModule {
    @Override
    public void applyOptions(@NonNull Context context, @NonNull GlideBuilder builder) {
        super.applyOptions(context, builder);
    }

    @Override
    public void registerComponents(
        @NonNull Context context, @NonNull Glide glide, @NonNull Registry registry) {
        registry.prepend(
            GlideUrl.class,
            InputStream.class,
            new EncryptionModelLoaderFactory(new NyxSettingsImpl(new DefaultPreference(context)))
        );
    }
}
