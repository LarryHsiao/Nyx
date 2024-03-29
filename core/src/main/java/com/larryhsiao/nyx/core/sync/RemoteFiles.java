package com.larryhsiao.nyx.core.sync;

import com.larryhsiao.clotho.Source;

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
    void post(String path, Source<InputStream> streamSource);

    /**
     * Delete given file.
     */
    void delete(String path);

    /**
     * Check if file exist
     */
    boolean exist(String path);
}
