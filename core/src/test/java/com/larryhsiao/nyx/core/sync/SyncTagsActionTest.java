package com.larryhsiao.nyx.core.sync;

import com.larryhsiao.clotho.database.SingleConn;
import com.larryhsiao.nyx.core.LocalNyx;
import com.larryhsiao.nyx.core.Nyx;
import com.larryhsiao.nyx.core.NyxDb;
import com.larryhsiao.nyx.core.attachments.FileAttachments;
import com.larryhsiao.nyx.core.tags.ConstTag;
import com.larryhsiao.nyx.core.tags.Tag;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.util.Collections;

/**
 * Test for {@link SyncTagsAction}.
 */
class SyncTagsActionTest {

    /**
     * New tags at remote.
     */
    @Test
    void newTagsAtRemote() throws IOException {
        final Nyx localNyx = new LocalNyx(
            new SingleConn(new NyxDb(Files.createTempFile("db", "").toFile(), true)),
            new FileAttachments(Files.createTempFile("temp", "attachments").toFile())
        );
        new SyncTagsAction(
            localNyx.tags(),
            new MemoryIndexes(
                Collections.emptyList(),
                Collections.singletonList(new ConstTag(1, "title", 1, true))
            )
        ).fire();
        Assertions.assertEquals(1, localNyx.tags().all().size());
    }

    /**
     * Test when there is a newer Tag at remote.
     */
    @Test
    void newTagUpdateAtRemote() throws Exception {
        final Nyx localNyx = new LocalNyx(
            new SingleConn(new NyxDb(Files.createTempFile("db", "").toFile(), true)),
            new FileAttachments(Files.createTempFile("temp", "attachments").toFile())
        );
        localNyx.tags().create(new ConstTag(-1, "title", 0, false));
        new SyncTagsAction(
            localNyx.tags(),
            new MemoryIndexes(
                Collections.emptyList(),
                Collections.singletonList(new ConstTag(1, "title2", 2, true))
            )
        ).fire();
        Assertions.assertEquals(1, localNyx.tags().all().size());
        final Tag dbTag = localNyx.tags().all().get(0);
        Assertions.assertEquals("title2", dbTag.title());
        Assertions.assertEquals(2, dbTag.version());
        Assertions.assertTrue(dbTag.deleted());
    }

    /**
     * New tags at local.
     */
    @Test
    void newTagsAtLocal() throws IOException {
        final Nyx localNyx = new LocalNyx(
            new SingleConn(new NyxDb(Files.createTempFile("db", "").toFile(), true)),
            new FileAttachments(Files.createTempFile("temp", "attachments").toFile())
        );
        final NyxIndexes remoteIndexes =
            new MemoryIndexes(Collections.emptyList(), Collections.emptyList());
        localNyx.tags().create(new ConstTag(-1, "title", 0, false));
        new SyncTagsAction(localNyx.tags(), remoteIndexes).fire();
        Assertions.assertEquals(1, localNyx.tags().all().size());
        Assertions.assertEquals(1, remoteIndexes.tags().size());
    }

    /**
     * New version at local.
     */
    @Test
    void newTagUpdateAtLocal() throws IOException {
        final Nyx localNyx = new LocalNyx(
            new SingleConn(new NyxDb(Files.createTempFile("db", "").toFile(), true)),
            new FileAttachments(Files.createTempFile("temp", "attachments").toFile())
        );
        localNyx.tags().update( // Update once for increase version
            localNyx.tags().create(new ConstTag(-1, "title", 1, false))
        );
        final NyxIndexes remoteIndexes = new MemoryIndexes(
            Collections.emptyList(),
            Collections.singletonList(new ConstTag(1, "title2", 1, true))
        );
        new SyncTagsAction(localNyx.tags(), remoteIndexes).fire();
        Assertions.assertEquals(1, localNyx.tags().all().size());
        Assertions.assertEquals(1, remoteIndexes.tags().size());
        final Tag remoteTag = remoteIndexes.tags().get(0);
        Assertions.assertEquals("title", remoteTag.title());
        Assertions.assertEquals(2, remoteTag.version());
        Assertions.assertFalse(remoteTag.deleted());
    }
}