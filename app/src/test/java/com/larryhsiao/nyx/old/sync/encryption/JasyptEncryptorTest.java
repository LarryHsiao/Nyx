package com.larryhsiao.nyx.old.sync.encryption;

import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertFalse;

/**
 * Unit-test for the class {@link JasyptEncryptor}
 */
public class JasyptEncryptorTest {
    /**
     * Check if the content encrypted
     */
    @Test
    public void encryptedNotOriginal() {
        JasyptEncryptor encrypt = new JasyptEncryptor("KEY");
        assertFalse(
            Arrays.equals(
                "INPUT".getBytes(),
                encrypt.encrypt("INPUT".getBytes())
            )
        );
    }

    /**
     * Check if encrypted can be decrypted
     */
    @Test
    public void decryption() {
        JasyptEncryptor encryptor = new JasyptEncryptor("KEY");
        assertArrayEquals(
            "INPUT".getBytes(),
            encryptor.decrypt(
                encryptor.encrypt("INPUT".getBytes())
            )
        );
    }
}