package com.larryhsiao.nyx.old.sync.encryption;

import org.jasypt.encryption.pbe.PooledPBEByteEncryptor;
import org.jasypt.iv.RandomIvGenerator;

/**
 * Jasypt implementation of {@link Encryptor}.
 */
public class JasyptEncryptor implements Encryptor<byte[]> {
    private final String key;
    private PooledPBEByteEncryptor encryptor = null;

    public JasyptEncryptor(String key) {
        this.key = key;
    }

    @Override
    public byte[] encrypt(byte[] input) {
        return getEncryptor().encrypt(input);
    }

    @Override
    public byte[] decrypt(byte[] input) {
        return getEncryptor().decrypt(input);
    }

    private PooledPBEByteEncryptor getEncryptor() {
        if (encryptor == null) {
            this.encryptor = new PooledPBEByteEncryptor();
            this.encryptor.setPoolSize(Runtime.getRuntime().availableProcessors());
            this.encryptor.setAlgorithm("PBEWithMD5AndDES");
            this.encryptor.setPassword(key);
            this.encryptor.setIvGenerator(new RandomIvGenerator());
        }
        return encryptor;
    }
}
