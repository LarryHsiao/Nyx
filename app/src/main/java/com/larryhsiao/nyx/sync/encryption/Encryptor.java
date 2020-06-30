package com.larryhsiao.nyx.sync.encryption;

/**
 * Encryptor obj.
 */
public interface Encryptor<T> {
    T encrypt(T input);
    T decrypt(T input);
}
