package com.larryhsiao.nyx.syncs;

import android.content.Context;
import com.larryhsiao.clotho.Action;

public class DropboxAuthCodeFlow implements Action {
    private final Context context;

    public DropboxAuthCodeFlow(Context context) {
        this.context = context;
    }

    @Override
    public void fire() {
        // @todo #0 Auth code flow.
    }
}
