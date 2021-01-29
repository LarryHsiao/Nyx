package com.larryhsiao.nyx.core.sync;

import com.larryhsiao.nyx.core.MemoryNyx;
import com.larryhsiao.nyx.core.Nyx;
import com.larryhsiao.nyx.core.jots.ConstJot;
import com.larryhsiao.nyx.core.jots.Jot;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

/**
 * Tests for {@link NyxSync}.
 */
class NyxSyncTest {

    /**
     * Test if new jot will be sync to another one.
     */
    @Test
    void newJotsToSecond() {
        final Nyx nyx1 = new MemoryNyx();
        nyx1.jots().create(new ConstJot(1, "title", "content", 100));
        final Nyx nyx2 = new MemoryNyx();
        new NyxSync(nyx1, nyx2).sync();

        List<Jot> jots1 = nyx1.jots().all();
        List<Jot> jots2 = nyx2.jots().all();
        Assertions.assertEquals(jots1.size(), jots2.size());
    }

    /**
     * Test if new Jot at second one will be sync to first one.
     */
    @Test
    void newJotToFirst() {
        final Nyx nyx1 = new MemoryNyx();
        final Nyx nyx2 = new MemoryNyx();
        nyx2.jots().create(new ConstJot(1, "title", "content", 100));
        new NyxSync(nyx1, nyx2).sync();

        final List<Jot> jots1 = nyx1.jots().all();
        final List<Jot> jots2 = nyx2.jots().all();
        Assertions.assertEquals(jots1.size(), jots2.size());
    }

    /**
     * Test if new version of jot will be sync to second one from first one.
     */
    @Test
    void updateByVersionToSecondOne() {
        final Nyx nyx1 = new MemoryNyx();
        nyx1.jots().create(new ConstJot(
            1,
            "title",
            "content",
            100,
            new double[0],
            "mood",
            2
        ));
        final Nyx nyx2 = new MemoryNyx();
        nyx2.jots().create(new ConstJot(
            1,
            "title2",
            "content2",
            100
        ));
        new NyxSync(nyx1, nyx2).sync();

        final Jot jot1 = nyx1.jots().byId(1);
        final Jot jot2 = nyx2.jots().byId(1);

        Assertions.assertEquals(jot1.title(), jot2.title());
        Assertions.assertEquals(jot1.content(), jot2.content());
        Assertions.assertEquals(jot1.createdTime(), jot2.createdTime());
        Assertions.assertEquals(jot1.mood(), jot2.mood());
    }

    /**
     * Test if new version of jot will be sync to first one from second one.
     */
    @Test
    void updateByVersionToFirstOne() {
        final Nyx nyx1 = new MemoryNyx();
        nyx1.jots().create(new ConstJot(
            1,
            "title",
            "content",
            100,
            new double[0],
            "mood"
        ));
        final Nyx nyx2 = new MemoryNyx();
        nyx2.jots().create(new ConstJot(
            1,
            "title2",
            "content2",
            100,
            new double[0],
            "mood",
            2
        ));
        new NyxSync(nyx1, nyx2).sync();

        final Jot jot1 = nyx1.jots().byId(1);
        final Jot jot2 = nyx2.jots().byId(1);

        Assertions.assertEquals(jot1.title(), jot2.title());
        Assertions.assertEquals(jot1.content(), jot2.content());
        Assertions.assertEquals(jot1.createdTime(), jot2.createdTime());
        Assertions.assertEquals(jot1.mood(), jot2.mood());
    }
}