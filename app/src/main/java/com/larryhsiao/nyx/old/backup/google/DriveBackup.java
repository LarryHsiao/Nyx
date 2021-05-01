package com.larryhsiao.nyx.old.backup.google;

import android.content.Context;
import android.net.Uri;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.larryhsiao.nyx.core.attachments.AllAttachments;
import com.larryhsiao.nyx.core.attachments.Attachment;
import com.larryhsiao.nyx.core.attachments.QueriedAttachments;
import com.larryhsiao.nyx.old.attachments.TempAttachmentFile;
import com.larryhsiao.nyx.old.backup.Backup;
import com.larryhsiao.aura.uri.UriMimeType;
import com.larryhsiao.clotho.Source;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * Implemented with Google Drive.
 */
public class DriveBackup implements Backup {
    private static final String BACKUP_FILE_NAME = "jot_backup.sql";
    private final Context context;
    private final Source<Connection> db;
    private final DriveFiles driveFiles;

    public DriveBackup(
        Context context,
        Source<Connection> db,
        DriveFiles driveFiles
    ) {
        this.context = context;
        this.db = db;
        this.driveFiles = driveFiles;
    }

    @Override
    public void save() {
        try {
            backupDb();
            backupFiles();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void backupDb() throws IOException, SQLException {
        String id = driveFiles.touch(BACKUP_FILE_NAME, "*/*");
        java.io.File dbBackup = new java.io.File(context.getFilesDir(), BACKUP_FILE_NAME);
        db.value().createStatement().executeQuery(
            // language=H2
            String.format("SCRIPT TO '%s'", dbBackup.getAbsolutePath())
        );
        driveFiles.save(
            id,
            BACKUP_FILE_NAME,
            new FileInputStream(dbBackup),
            "*/*"
        );
        dbBackup.delete();
    }

    private void backupFiles() throws IOException {
        for (Attachment attachment : new QueriedAttachments(new AllAttachments(db, true)).value()) {
            Uri uri = Uri.parse(attachment.uri());
            final String mimeType = new UriMimeType(context, uri.toString()).value();
            List<File> exist = driveFiles.byName(uri.getLastPathSegment());
            if (attachment.deleted()) {
                if (!exist.isEmpty()) {
                    driveFiles.delete(exist.get(0).getId());
                }
            } else {
                backupFile(exist, mimeType, uri);
            }
        }
    }

    private void backupFile(List<File> exist, String mimeType, Uri uri) throws IOException {
        if (exist.isEmpty()) {
            driveFiles.save(
                driveFiles.touch(uri.getLastPathSegment(), mimeType),
                uri.getLastPathSegment(),
                context.getContentResolver().openInputStream(uri),
                mimeType
            );
        } else {
            // @todo #0 Check the remote file and locale file are same.
        }
    }

    @Override
    public void restore() {
        try {
            final java.io.File localRoot = new java.io.File(
                context.getFilesDir(),
                "attachments"
            );
            localRoot.mkdir();
            restoreDb();
            restoreAttachment(driveFiles.files());
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }

    private void restoreAttachment(FileList fileList) throws IOException {
        for (File file : fileList.getFiles()) {
            java.io.File target = new java.io.File(
                new java.io.File(context.getFilesDir(), "attachments"),
                file.getName()
            );
            if (file.getName().equals(BACKUP_FILE_NAME) || target.exists()) {
                continue;
            }
            driveFiles.download(file.getId(), target);
        }
        String nextToken = fileList.getNextPageToken();
        if (nextToken != null && !nextToken.isEmpty()) {
            restoreAttachment(driveFiles.files(nextToken));
        }
    }

    private void restoreDb() throws IOException, SQLException {
        final java.io.File target = new TempAttachmentFile(
            context,
            "tempDb.sql"
        ).value();
        driveFiles.download(
            driveFiles.touch(BACKUP_FILE_NAME, "*/*"),
            target
        );
        db.value().createStatement().execute("drop all objects delete files");
        db.value().createStatement()
            .executeUpdate(
                // language=H2
                String.format("RUNSCRIPT FROM '%s';", target.getAbsolutePath())
            );
    }
}
