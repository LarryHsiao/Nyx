package com.larryhsiao.nyx.core.sync;

import com.larryhsiao.nyx.core.sync.network.AllBroadcastAddresses;
import com.larryhsiao.nyx.core.sync.network.Broadcasting;

import java.net.InetAddress;

/**
 * Pining in a fixed interval.
 */
class Pinging {
    private final long interval;
    private final int port;
    private boolean running = false;

    Pinging(long interval, int port) {
        this.interval = interval;
        this.port = port;
    }

    /**
     * To determine if this pining object is running.
     */
    public boolean isRunning() {
        return running;
    }

    /**
     * Fire up the pining threads, resource and procedures.
     */
    public void launch() {
        running = true;
        new Thread(() -> {
            while (running) {
                try {
                    for (InetAddress inetAddress : new AllBroadcastAddresses().value()) {
                        new Broadcasting(
                            "ping",
                            inetAddress,
                            port
                        ).fire();
                    }
                    try {
                        Thread.sleep(interval);
                    } catch (Exception ignore) {
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * Shutdown this object and its threads.
     * Note: The pinging loop will still do a full circle.
     */
    public void shutdown() {
        running = false;
    }
}
