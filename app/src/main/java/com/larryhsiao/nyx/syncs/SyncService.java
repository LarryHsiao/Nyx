package com.larryhsiao.nyx.syncs;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import androidx.core.app.JobIntentService;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import com.larryhsiao.nyx.NyxApplication;
import com.larryhsiao.nyx.R;
import com.larryhsiao.nyx.core.sync.Syncs;
import org.jetbrains.annotations.NotNull;

import static com.larryhsiao.nyx.NyxApplication.*;

/**
 * Service for doing a sync.
 */
public class SyncService extends JobIntentService {
    public static void enqueueWork(Context context) {
        enqueueWork(context, SyncService.class, SERVICE_ID_SYNC, new Intent());
    }

    @Override
    protected void onHandleWork(@NotNull Intent intent) {
        try {
            showSyncingNotification();
            final Syncs syncs = ((NyxApplication) getApplication()).getSyncs();
            syncs.sync();
            dismissSyncNotification();
        } catch (Exception e) {
            e.printStackTrace();
            // @todo #100 Publish failure of syncs.
        }
    }

    private void showSyncingNotification() {
        createNotificationChannel();
        NotificationManagerCompat.from(this).notify(
            NOTIFICATION_ID_SYNC,
            new NotificationCompat.Builder(this, CHANNEL_ID_SYNC)
                .setSmallIcon(R.drawable.ic_sync)
                .setContentTitle(getString(R.string.Sync))
                .setContentText(getString(R.string.Jotted_is_syncing))
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setAutoCancel(false)
                .setOngoing(true)
                .build()
        );
    }

    private void dismissSyncNotification() {
        NotificationManagerCompat.from(this).cancel(NOTIFICATION_ID_SYNC);
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_LOW;
            NotificationChannel channel = new NotificationChannel(
                NyxApplication.CHANNEL_ID_SYNC,
                getString(R.string.Sync),
                importance
            );
            channel.setDescription(getString(R.string.service_for_syncing));
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
