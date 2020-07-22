package com.larryhsiao.nyx.sync.encryption;

import com.silverhetch.clotho.Source;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.MessageDigest;
import java.util.Arrays;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * Source to build encryption key.
 */
public class EncryptionKeySrc implements Source<SecretKey> {
    private final String keyStr;

    public EncryptionKeySrc(String keyStr) {
        this.keyStr = keyStr;
    }

    @Override
    public SecretKey value() {
        try {
            return new SecretKeySpec(Arrays.copyOf(
                MessageDigest.getInstance("SHA-1").digest(keyStr.getBytes(UTF_8)),
                16
            ), "AES");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
