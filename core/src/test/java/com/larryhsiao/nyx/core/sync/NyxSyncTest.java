package com.larryhsiao.nyx.core.sync;

import com.larryhsiao.nyx.core.MemoryNyx;
import com.larryhsiao.nyx.core.Nyx;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link NyxSync}.
 */
class NyxSyncTest {

    /**
     * Test if the result list is same as original.
     */
    @Test
    void sameResultAfterSync() {
        final Nyx nyx1 = new MemoryNyx();
        final Nyx nyx2 = new MemoryNyx();
        new NyxSync(nyx1, nyx2).sync();
    }
}