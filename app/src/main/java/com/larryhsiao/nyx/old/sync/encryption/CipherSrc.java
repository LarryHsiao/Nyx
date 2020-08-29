package com.larryhsiao.nyx.old.sync.encryption;

import com.silverhetch.clotho.Source;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;

/**
 * Source to build cipher object for encryption.
 */
public class CipherSrc implements Source<Cipher> {
    private final int opMode;
    private final Source<SecretKey> key;

    public CipherSrc(int opMode, Source<SecretKey> key) {
        this.opMode = opMode;
        this.key = key;
    }

    @Override
    public Cipher value() {
        try {
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(opMode, key.value());
            return cipher;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
