package com.larryhsiao.nyx.core.sync.dropbox;

import com.larryhsiao.nyx.core.sync.RemoteFiles;

import java.io.InputStream;

public class DropboxRemoteFiles implements RemoteFiles {
    private static final String PATH_PREFIX = "/jotted_nyx";
    private final String token;

    public DropboxRemoteFiles(String token) {
        this.token = token;
    }

    @Override
    public InputStream get(String path) {
        return new DBFileStream(token, PATH_PREFIX + path).value();
    }

    @Override
    public void post(String path, InputStream inputStream) {
        new DBFileUploading(token, path, inputStream).fire();
    }

    @Override
    public void delete(String path) {
        new DBFileDeletion(token, path).fire();
    }
}
