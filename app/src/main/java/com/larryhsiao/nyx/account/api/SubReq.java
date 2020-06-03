package com.larryhsiao.nyx.account.api;

/**
 * Request DTO for Subscription api.
 */
public class SubReq {
    /**
     * The user id.
     */
    public String uid;
    /**
     * The purchased product id.
     */
    public String sku_id;
    /**
     * The purchase token return from google play billing.
     */
    public String purchase_token;
    /**
     * Boolean to confirm to transfer user to new user.
     */
    public boolean changeUser;
}
