package com.larryhsiao.nyx.core.sync.dropbox;

import com.larryhsiao.nyx.core.sync.RemoteFiles;

import java.io.InputStream;

/**
 * Dropbox's implementation of {@link RemoteFiles}.
 */
public class DropboxRemoteFiles implements RemoteFiles {
    private final String token;

    public DropboxRemoteFiles(String token) {
        this.token = token;
    }

    @Override
    public InputStream get(String path) {
        return new DBFileStream(token, path).value();
    }

    @Override
    public void post(String path, InputStream inputStream) {
        new DBFileUploading(token, path, inputStream).fire();
    }

    @Override
    public void delete(String path) {
        new DBFileDeletion(token, path).fire();
    }

    @Override
    public boolean exist(String path) {
        return new DBFileExist(token, path).value();
    }
}
