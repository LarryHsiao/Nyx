package com.larryhsiao.nyx.core.sync;

import java.io.InputStream;

/**
 * Remote file-base storage.
 */
public interface RemoteFiles {
    /**
     * @param path Path at remote.
     */
    InputStream get(String path);

    /**
     * Upload given data, replace exist one.
     */
    void post(String path, InputStream inputStream);

    /**
     * Delete given file.
     */
    void delete(String path);
}
