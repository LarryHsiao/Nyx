package com.larryhsiao.nyx.account.api;

import androidx.annotation.Keep;

/**
 * DTO of changing encrypt key request.
 */
@Keep
public class ChangeEncryptKeyReq {
    /**
     * The uid of the user.
     */
    public String uid;

    /**
     * The new hash of encryption key.
     */
    public String keyHash;
}
