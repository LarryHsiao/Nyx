package com.larryhsiao.nyx;

/**
 * Ids for notification.
 */
public interface NotificationIds {
    /**
     * @see com.larryhsiao.nyx.backup.google.DriveBackupService
     */
    String CHANNEL_ID_BACKUP_DRIVE = "BackupDrive";
    /**
     * @see com.larryhsiao.nyx.sync.SyncService
     */
    String CHANNEL_ID_SYNC = "Sync";

    /**
     * @see com.larryhsiao.nyx.backup.google.DriveBackupService
     */
    int NOTIFICATION_ID_BACKUP_DRIVE = 1000;

    /**
     * @see com.larryhsiao.nyx.sync.SyncService
     */
    int NOTIFICATION_ID_INVALID_ENCRYPT_KEY = 1001;
}
