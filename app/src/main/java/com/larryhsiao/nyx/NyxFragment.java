package com.larryhsiao.nyx;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.larryhsiao.clotho.storage.Ceres;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Fragment that Nyx using.
 */
public abstract class NyxFragment extends Fragment {
    private ScheduledExecutorService workerThread;
    private NyxApplication app = null;
    private Ceres storage;

    @Override
    public void onCreate(@Nullable  Bundle savedInstanceState) {
        workerThread = Executors.newScheduledThreadPool(4);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onDestroy() {
        workerThread.shutdown();
        workerThread = null;
        super.onDestroy();
    }

    protected void async(Runnable runnable) {
        workerThread.execute(runnable);
    }

    protected NyxApplication getApp() {
        if (app == null) {
            app = (NyxApplication) requireContext().getApplicationContext();
        }
        return app;
    }

    protected Ceres getStorage() {
        if (storage == null) {
            storage = ((NyxApplication) requireContext().getApplicationContext()).getStorage();
        }
        return storage;
    }

    /**
     * @param canChangeCallback If the page not require action before change page, the callback will invoked.
     * @return If the page can be changed.
     */
    public boolean preferChangePage(Runnable canChangeCallback) {
        canChangeCallback.run();
        return true;
    }
}
