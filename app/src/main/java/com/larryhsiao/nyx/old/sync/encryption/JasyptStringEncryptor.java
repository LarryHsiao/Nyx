package com.larryhsiao.nyx.old.sync.encryption;

import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
import org.jasypt.iv.RandomIvGenerator;

/**
 * Jasypt implementation of {@link Encryptor}.
 */
public class JasyptStringEncryptor implements Encryptor<String> {
    private final String key;
    private PooledPBEStringEncryptor encryptor = null;

    public JasyptStringEncryptor(String key) {
        this.key = key;
    }

    @Override
    public String encrypt(String input) {
        return getEncryptor().encrypt(input);
    }

    @Override
    public String decrypt(String input) {
        return getEncryptor().decrypt(input);
    }

    private PooledPBEStringEncryptor getEncryptor() {
        if (encryptor == null) {
            this.encryptor = new PooledPBEStringEncryptor();
            this.encryptor.setPoolSize(Runtime.getRuntime().availableProcessors());
            this.encryptor.setAlgorithm("PBEWithMD5AndDES");
            this.encryptor.setPassword(key);
            this.encryptor.setIvGenerator(new RandomIvGenerator());
        }
        return encryptor;
    }
}
