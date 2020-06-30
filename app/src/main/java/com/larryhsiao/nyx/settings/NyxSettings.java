package com.larryhsiao.nyx.settings;

/**
 * Nyx settings.
 */
public interface NyxSettings {
    /**
     * Current Bio Auth status.
     */
    boolean bioAuthEnabled();

    /**
     * The encryption key for encrypting user generated data.
     */
    String encryptionKey();

    /**
     * The Image quality user prefer to upload. Note: only affected to Jpeg.
     */
    Quality ImageQuality();

    /**
     * Quality of stored image quality.
     */
    enum Quality {
        /**
         * Almost no quality loss, so as size of image.
         */
        HIGH,
        /**
         * Balanced quality/size consume.
         */
        GOOD,
        /**
         * Noticeable quality loss, with significant size reduced.
         */
        LOW
    }
}
