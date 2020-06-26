package com.larryhsiao.nyx.sync.encryption;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.iv.RandomIvGenerator;

/**
 * Jasypt implementation of {@link StringEncryptor}.
 */
public class JasyptStringEncryptor implements StringEncryptor {
    private final String key;
    private StandardPBEStringEncryptor encryptor = null;

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

    private StandardPBEStringEncryptor getEncryptor() {
        if (encryptor == null) {
            this.encryptor = new StandardPBEStringEncryptor();
            this.encryptor.setAlgorithm("PBEWithMD5AndDES");
            this.encryptor.setPassword(key);
            this.encryptor.setIvGenerator(new RandomIvGenerator());
        }
        return encryptor;
    }
}
