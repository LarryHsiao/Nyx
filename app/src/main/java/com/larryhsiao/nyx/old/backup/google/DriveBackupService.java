package com.larryhsiao.nyx.old.backup.google;

import android.app.NotificationChannel;
import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.larryhsiao.nyx.JotApplication;
import com.larryhsiao.nyx.R;
import com.larryhsiao.nyx.old.NotificationIds;
import com.larryhsiao.nyx.old.ServiceIds;
import com.larryhsiao.nyx.old.backup.Backup;

import static android.app.NotificationManager.IMPORTANCE_LOW;
import static android.os.Build.VERSION.SDK_INT;
import static android.os.Build.VERSION_CODES.O;
import static androidx.core.app.NotificationCompat.PRIORITY_DEFAULT;
import static com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential.usingOAuth2;
import static com.google.api.services.drive.DriveScopes.DRIVE_FILE;
import static java.util.Collections.singleton;

/**
 * Service for Backup/Restore from Drive.
 */
public class DriveBackupService extends JobIntentService implements ServiceIds, NotificationIds {
    private static final String ACTION_BACKUP = "ACTION_BACKUP";
    private static final String ACTION_RESTORE = "ACTION_RESTORE";

    public static void enqueueBackup(Context context) {
        enqueueWork(context, DriveBackupService.class, BACKUP_DRIVE, new Intent(ACTION_BACKUP));
    }

    public static void enqueueRestore(Context context) {
        enqueueWork(context, DriveBackupService.class, BACKUP_DRIVE, new Intent(ACTION_RESTORE));
    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if (account == null || intent.getAction() == null) {
            return;
        }
        Backup backup = new DriveBackup(
            this,
            ((JotApplication) getApplication()).getDb(),
            new DriveFilesImpl(
                new Drive.Builder(
                    AndroidHttp.newCompatibleTransport(),
                    new GsonFactory(),
                    credential(account)
                ).setApplicationName(
                    getString(R.string.app_name)
                ).build(),
                "backup_jots"
            )
        );
        switch (intent.getAction()) {
            case ACTION_BACKUP:
                notification(getString(R.string.BackingUp), false);
                backup.save();
                notification(getString(R.string.Backup_done), true);
                break;
            case ACTION_RESTORE:
                notification(getString(R.string.Restoring), false);
                backup.restore();
                notification(getString(R.string.Restored), true);
                break;
            default:
                break;
        }
    }

    private void notification(String title, boolean finished) {
        NotificationManagerCompat manager = NotificationManagerCompat.from(this);
        NotificationCompat.Builder builder = notification(manager);
        builder.setContentTitle(title);
        if (finished) {
            builder.setProgress(0, 0, false);
            builder.setOngoing(false);
        } else {
            builder.setProgress(0, 0, true);
            builder.setOngoing(true);
        }
        manager.notify(NOTIFICATION_ID_BACKUP_DRIVE, builder.build());
    }

    private NotificationCompat.Builder notification(NotificationManagerCompat notificationManager) {
        if (SDK_INT >= O) {
            NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID_BACKUP_DRIVE,
                getString(R.string.Backup_service_Drive_),
                IMPORTANCE_LOW
            );
            channel.setDescription(getString(R.string.Backup_Restore_Jots_to_drives));
            notificationManager.createNotificationChannel(channel);
        }
        return new NotificationCompat.Builder(
            this,
            CHANNEL_ID_BACKUP_DRIVE
        ).setSmallIcon(R.drawable.ic_jotted)
            .setPriority(PRIORITY_DEFAULT)
            .setAutoCancel(false);
    }

    private GoogleAccountCredential credential(GoogleSignInAccount account) {
        GoogleAccountCredential credential = usingOAuth2(this, singleton(DRIVE_FILE));
        credential.setSelectedAccount(account.getAccount());
        return credential;
    }

}
