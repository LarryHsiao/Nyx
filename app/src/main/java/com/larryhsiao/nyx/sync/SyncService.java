package com.larryhsiao.nyx.sync;

import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.larryhsiao.nyx.JotApplication;
import com.silverhetch.clotho.Source;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Service to sync data to server.
 *
 * @todo #1 Inform user to resolve conflict if the local data will be override.
 * @todo #2 Duplicate files when sync files.
 */
public class SyncService extends JobIntentService {
    private static final int JOB_ID = 1000;

    public static void enqueue(Context context) {
        enqueueWork(context, SyncService.class, JOB_ID, new Intent());
    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        final Source<Connection> db = ((JotApplication) getApplication()).db;
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            return;
        }
        new SyncJots(user.getUid(), db).fire();
        new SyncTags(user.getUid(), db).fire();
        new SyncTagJot(user.getUid(), db).fire();
        new SyncAttachments(this, user.getUid(), db, true).fire();
    }
}
