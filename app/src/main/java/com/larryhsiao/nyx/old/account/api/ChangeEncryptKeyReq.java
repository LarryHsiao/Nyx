package com.larryhsiao.nyx.old.account.api;

import androidx.annotation.Keep;

/**
 * DTO of changing encrypt key request.
 */
@Keep
public class ChangeEncryptKeyReq {

    /**
     * The new hash of encryption key.
     */
    public String keyHash;
}
