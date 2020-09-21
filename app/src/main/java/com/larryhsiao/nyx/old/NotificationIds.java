package com.larryhsiao.nyx.old;

/**
 * Ids for notification.
 */
public interface NotificationIds {
    /**
     * @see com.larryhsiao.nyx.old.backup.google.DriveBackupService
     */
    String CHANNEL_ID_BACKUP_DRIVE = "BackupDrive";
    /**
     * @see com.larryhsiao.nyx.old.sync.SyncService
     */
    String CHANNEL_ID_SYNC = "Sync";

    /**
     * @see com.larryhsiao.nyx.old.sync.SyncService
     */
    String CHANNEL_ID_SYNCING = "Syncing";

    int NOTIFICATION_ID_BACKUP_DRIVE = 1000;

    /**
     * @see com.larryhsiao.nyx.old.sync.SyncService
     */
    int NOTIFICATION_ID_INVALID_ENCRYPT_KEY = 1001;
    /**
     * @see com.larryhsiao.nyx.old.sync.SyncService
     */
    int NOTIFICATION_ID_SYNCING = 1002;
}