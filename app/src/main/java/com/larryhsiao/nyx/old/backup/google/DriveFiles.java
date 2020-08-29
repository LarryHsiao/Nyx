package com.larryhsiao.nyx.old.backup.google;

import android.util.Pair;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Files for Google Drive.
 */
public interface DriveFiles {

    /**
     * Touch a file to the drive, and returns an id for the created file.
     */
    String touch(String name, String mimeType) throws IOException;

    /**
     * Opens the file identified by {@code fileId} and returns a {@link Pair} of its name and
     * contents.
     */
    void download(String fileId, java.io.File target) throws IOException;

    /**
     * Updates the file identified by {@code fileId} with the given {@code name} and {@code
     * content}.
     */
    void save(
        String fileId,
        String name,
        InputStream inputStream,
        String mimeType
    ) throws IOException;


    /**
     * Returns a {@link FileList} containing all the visible files in the user's My Drive.
     *
     * <p>The returned list will only contain files visible to this app, i.e. those which were
     * created by this app. To perform operations on files not created by the app, the project must
     * request Drive Full Scope in the <a href="https://play.google.com/apps/publish">Google
     * Developer's Console</a> and be submitted to Google for verification.</p>
     *
     * @param pageToken The token for fetching uncompleted list.
     */
    FileList files(String pageToken) throws IOException;

    /**
     * @see DriveFiles#files(String)
     */
    FileList files() throws IOException;

    /**
     * @param name Name of file in workspace.
     */
    List<File> byName(String name) throws IOException;

    /**
     * Delete a file by fileId.
     */
    void delete(String fileId) throws IOException;

    /**
     * Clear all files in workspace.
     */
    void clear() throws IOException;
}
