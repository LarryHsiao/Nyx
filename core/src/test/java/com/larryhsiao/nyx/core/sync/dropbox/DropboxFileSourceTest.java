package com.larryhsiao.nyx.core.sync.dropbox;

import com.larryhsiao.clotho.io.ProgressedCopy;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Test for {@link DropboxFileSource}
 */
@Disabled
public class DropboxFileSourceTest {
    /**
     * Check if we can download the actual file.
     */
    @Test
    void download() throws IOException {
        final String token = "";
        final String dst = "";
        final String remotePath = "";
        new ProgressedCopy(
            new DropboxFileSource(token, remotePath).value(),
            new FileOutputStream(dst),
            4096,
            true,
            integer -> null
        ).fire();
    }
}
