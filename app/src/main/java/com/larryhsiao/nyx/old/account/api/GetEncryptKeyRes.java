package com.larryhsiao.nyx.old.account.api;

import androidx.annotation.Keep;

/**
 * DTO of Get /encryptKey
 */
@Keep
public class GetEncryptKeyRes {
    /**
     * The MD5 hash of encrypt key.
     */
    public String keyHash;
}
