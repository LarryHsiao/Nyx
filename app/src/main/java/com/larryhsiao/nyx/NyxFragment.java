package com.larryhsiao.nyx;

import androidx.fragment.app.Fragment;
import com.larryhsiao.clotho.storage.Ceres;

/**
 * Fragment that Nyx using.
 */
public abstract class NyxFragment extends Fragment {
    private NyxApplication app = null;
    private Ceres storage;

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
