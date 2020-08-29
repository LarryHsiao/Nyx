package com.larryhsiao.nyx.old.settings;

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
    Quality imageQuality();

    /**
     * Quality of stored image quality.
     */
    enum Quality {
        /**
         * Almost no quality loss, so as size of image.
         */
        HIGH(95),
        /**
         * Balanced quality/size consume.
         */
        GOOD(90),
        /**
         * Noticeable quality loss, with significant size reduced.
         */
        LOW(80);

        /**
         * The jpeg compressed quality.
         */
        public final int amount;

        Quality(int amount) {this.amount = amount;}
    }
}
