package com.larryhsiao.nyx.account.action;

import android.content.Context;
import android.content.Intent;
import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.larryhsiao.nyx.sync.SyncService;
import com.silverhetch.clotho.Action;

/**
 * Action to logout.
 */
public class LogoutAction implements Action {
    private final Context context;

    public LogoutAction(Context context) {
        this.context = context;
    }

    @Override
    public void fire() {
        context.stopService(new Intent(context, SyncService.class));
        AuthUI.getInstance().signOut(context);
        FirebaseAuth.getInstance().signOut();
    }
}