package com.larryhsiao.nyx.old.account.action;

import android.content.Context;
import androidx.preference.PreferenceManager;
import com.silverhetch.clotho.Action;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Action to update last synced time.
 */
public class UpdateLastSyncedAction implements Action {
    private final Context context;

    public UpdateLastSyncedAction(Context context) {
        this.context = context;
    }

    @Override
    public void fire() {
        PreferenceManager.getDefaultSharedPreferences(context)
            .edit()
            .putString("sync_now", SimpleDateFormat.getDateTimeInstance().format(new Date()))
            .apply();
    }
}
