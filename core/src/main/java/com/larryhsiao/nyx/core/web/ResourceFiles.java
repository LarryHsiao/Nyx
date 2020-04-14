package com.larryhsiao.nyx.core.web;

import java.io.IOException;
import java.io.InputStream;

/**
 * Interface for the static resources.
 */
public interface ResourceFiles {
    /**
     * Open InputStream of path
     */
    InputStream open(String path)throws IOException;
}
