package com.larryhsiao.nyx.old.account.api;

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
        req.changeUser = false;
        req.purchase_token = "token";
        req.sku_id = "premium";
        assertEquals(
            403,
            NyxApi.client().subscription("ID_TOKEN",req).execute().code()
        );
    }
}