package com.larryhsiao.nyx.thirdparty;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.json.Json;
import java.io.StringReader;

/**
 * (Learning/Edge) Test for javax.json.*;
 */
public class JavaxJsonTest {
    /**
     * Should have exception if the field we trying to get is not exist.
     */
    @Test
    void fieldNotExist() throws NullPointerException {
        Assertions.assertThrows(
            NullPointerException.class,
            () -> Json.createReader(
                new StringReader("{\"abc\":\"abc\"}")
            ).readObject()
                .getString("content")
        );
    }

    /**
     * Test if type mismatch.
     */
    @Test
    void typeNotMatch() {
        Assertions.assertThrows(
            ClassCastException.class,
            () -> Json.createReader(
                new StringReader("{\"abc\":\"abc\"}")
            ).readObject()
                .getJsonNumber("abc")
                .longValue()
        );
    }
}
