package com.larryhsiao.nyx;

import androidx.fragment.app.Fragment;

/**
 * Fragment that Nyx using.
 */
public abstract class NyxFragment extends Fragment {
    private NyxApplication app = null;

    protected NyxApplication getApp() {
        if (app == null) {
            app = (NyxApplication) requireContext().getApplicationContext();
        }
        return app;
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
