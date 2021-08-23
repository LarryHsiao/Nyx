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
        return new DropboxFileSource(token, PATH_PREFIX + path).value();
    }

    @Override
    public void post(String path, InputStream inputStream) {

    }

    @Override
    public void delete(String path) {

    }
}
