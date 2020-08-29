package com.larryhsiao.nyx.old.sync.encryption;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

/**
 * Unit-test for the class {@link JasyptStringEncryptor}
 */
public class JasyptStringEncryptorTest {
    /**
     * Check if the content encrypted
     */
    @Test
    public void encryptedNotOriginal() {
        JasyptStringEncryptor encrypt = new JasyptStringEncryptor("KEY");
        assertNotEquals(
            "INPUT",
            encrypt.encrypt("INPUT")
        );
    }

    /**
     * Check if encrypted can be decrypted
     */
    @Test
    public void decryption() {
        JasyptStringEncryptor encryptor = new JasyptStringEncryptor("KEY");
        assertEquals(
            "INPUT",
            encryptor.decrypt(encryptor.encrypt("INPUT"))
        );
    }
}