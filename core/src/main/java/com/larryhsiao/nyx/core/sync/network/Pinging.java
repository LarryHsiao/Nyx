package com.larryhsiao.nyx.core.sync.network;

import java.net.InetAddress;

/**
 * Pining in a fixed interval.
 */
public class Pinging {
    private final long interval;
    private final int port;
    private boolean running = false;

    public Pinging(long interval, int port) {
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
                        new UdpSending(
                            new Packet("ping", inetAddress, port), true
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
