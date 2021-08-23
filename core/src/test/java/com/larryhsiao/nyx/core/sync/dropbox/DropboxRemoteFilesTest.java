package com.larryhsiao.nyx.core.sync.dropbox;

import com.larryhsiao.clotho.io.ProgressedCopy;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Test for {@link DropboxRemoteFiles}
 */
@Disabled
public class DropboxRemoteFilesTest {
    private static final String token = "";
    private static final String downloadDst = "/home/larryhsiao/file.txt";
    private static final String remotePath = "/jotted_nyx/file.txt";

    /**
     * Check if we can upload file.
     */
    @Test
    void upload() throws IOException{
        new DBFileUploading(
            token,
            remotePath,
            new ByteArrayInputStream("file text".getBytes())
        ).fire();
        download(); // can be downloaded normally
        Assertions.assertTrue(true);
    }

    /**
     * Check if we can download the actual file.
     */
    @Test
    void download() throws IOException {
        new ProgressedCopy(
            new DBFileStream(token, remotePath).value(),
            new FileOutputStream(downloadDst),
            4096,
            true,
            integer -> null
        ).fire();
        Assertions.assertTrue(new File(downloadDst).exists());
    }

    /**
     * Delete file at remote
     */
    @Test
    void delete() {
        new DBFileDeletion(token, remotePath).fire();
        try{
            download();
            Assertions.fail();
        }catch (Exception e){
            Assertions.assertTrue(true);
        }
    }
}
