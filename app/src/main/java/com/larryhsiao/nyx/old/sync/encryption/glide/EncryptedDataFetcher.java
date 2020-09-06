package com.larryhsiao.nyx.old.sync.encryption.glide;

import androidx.annotation.NonNull;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.data.DataFetcher;
import com.bumptech.glide.load.model.GlideUrl;
import com.larryhsiao.nyx.old.settings.NyxSettings;
import com.larryhsiao.nyx.old.sync.encryption.CipherSrc;
import com.larryhsiao.nyx.old.sync.encryption.EncryptionKeySrc;

import javax.crypto.CipherInputStream;
import java.io.InputStream;

import static javax.crypto.Cipher.DECRYPT_MODE;

/**
 * DataFetcher for decoding firebase images.
 */
public class EncryptedDataFetcher implements DataFetcher<InputStream> {
    private final NyxSettings settings;
    private final GlideUrl model;
    private InputStream inputStream;

    public EncryptedDataFetcher(NyxSettings settings, GlideUrl model) {
        this.settings = settings;
        this.model = model;
    }

    @Override
    public void loadData(
        @NonNull Priority priority,
        @NonNull DataCallback<? super InputStream> callback
    ) {
        try {
            callback.onDataReady(inputStream = new CipherInputStream(
                model.toURL().openConnection().getInputStream(),
                new CipherSrc(
                    DECRYPT_MODE,
                    new EncryptionKeySrc(settings.encryptionKey())
                ).value()
            ));
        } catch (Exception e) {
            callback.onLoadFailed(e);
        }
    }

    @Override
    public void cleanup() {

    }

    @Override
    public void cancel() {
        try {
            if (inputStream != null) {
                inputStream.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @NonNull
    @Override
    public Class<InputStream> getDataClass() {
        return InputStream.class;
    }

    @NonNull
    @Override
    public DataSource getDataSource() {
        return DataSource.REMOTE;
    }
}
