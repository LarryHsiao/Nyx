package com.larryhsiao.nyx.core.sync;

/**
 * A Remote Nyx device found by network or any available protocol.
 */
public interface NyxRemote {
    /**
     * Name of this device
     */
    String name();

    /**
     * Determine if the device is paired.
     */
    boolean isPaired();
}
