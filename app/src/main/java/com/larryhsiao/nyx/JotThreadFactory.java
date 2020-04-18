package com.larryhsiao.nyx;

import android.util.Log;

import java.util.concurrent.ThreadFactory;

public class JotThreadFactory implements ThreadFactory {
    @Override
    public Thread newThread(Runnable runnable) {
        Thread thread = new Thread(runnable);
        thread.setPriority(Thread.MIN_PRIORITY);

        thread.setUncaughtExceptionHandler((thread1, ex) -> Log.e(
            "JotThreadFactory",
            thread1.getName() + " encountered an error: " + ex.getMessage()
        ));
        return thread;
    }
}
