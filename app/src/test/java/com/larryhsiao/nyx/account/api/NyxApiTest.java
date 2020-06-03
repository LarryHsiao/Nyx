package com.larryhsiao.nyx.account.api;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Unit-test for the class {@link NyxApi}
 */
public class NyxApiTest {

    /**
     * Token
     */
    @Test
    public void invalidToken() throws Exception{
        SubReq req = new SubReq();
        req.uid="UID";
        req.changeUser = false;
        req.purchase_token = "token";
        req.sku_id = "premium";
        assertEquals(
            500,
            NyxApi.client().subscription(req).execute().code()
        );
    }
}