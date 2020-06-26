package com.larryhsiao.nyx.sync.encryption;

/**
 * StringEncryptor obj.
 */
public interface StringEncryptor {
    String encrypt(String input);
    String decrypt(String input);
}
