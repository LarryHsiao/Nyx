package com.larryhsiao.nyx.core.sync;

import com.larryhsiao.nyx.core.MemoryNyx;
import com.larryhsiao.nyx.core.Nyx;
import com.larryhsiao.nyx.core.jots.ConstJot;
import com.larryhsiao.nyx.core.jots.Jot;
import com.larryhsiao.nyx.core.tags.ConstJotTag;
import com.larryhsiao.nyx.core.tags.ConstTag;
import com.larryhsiao.nyx.core.tags.JotTag;
import com.larryhsiao.nyx.core.tags.Tag;
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
    void updateJotByVersionToSecondOne() {
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
    void updateJotByVersionToFirstOne() {
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

    /**
     * New tags will be sync to second one.
     */
    @Test
    void newTagsToSecond() {
        final Nyx nyx1 = new MemoryNyx();
        nyx1.tags().create(new ConstTag(1, "title", 1, false));
        final Nyx nyx2 = new MemoryNyx();
        new NyxSync(nyx1, nyx2).sync();

        List<Tag> tags1 = nyx1.tags().all();
        List<Tag> tags2 = nyx2.tags().all();
        Assertions.assertEquals(tags1.size(), tags2.size());
    }


    /**
     * Test if new Tag at second one will be sync to first one.
     */
    @Test
    void newTagToFirst() {
        final Nyx nyx1 = new MemoryNyx();
        final Nyx nyx2 = new MemoryNyx();
        nyx2.tags().create(new ConstTag(1, "title", 1, false));
        new NyxSync(nyx1, nyx2).sync();

        final List<Tag> tags1 = nyx1.tags().all();
        final List<Tag> tags2 = nyx2.tags().all();
        Assertions.assertEquals(tags1.size(), tags2.size());
    }

    /**
     * Test if new version of tag will be sync to second one from first one.
     */
    @Test
    void updateTagByVersionToSecondOne() {
        final Nyx nyx1 = new MemoryNyx();
        nyx1.tags().create(new ConstTag(1, "title", 2, false));
        final Nyx nyx2 = new MemoryNyx();
        nyx2.tags().create(new ConstTag(1, "title2", 1, false));
        new NyxSync(nyx1, nyx2).sync();

        final Tag tag1 = nyx1.tags().all().get(0);
        final Tag tag2 = nyx2.tags().all().get(0);

        Assertions.assertEquals(tag1.title(), tag2.title());
        Assertions.assertEquals(tag1.version(), tag2.version());
    }

    /**
     * Test if new version of jot will be sync to first one from second one.
     */
    @Test
    void updateTagByVersionToFirstOne() {
        final Nyx nyx1 = new MemoryNyx();
        nyx1.tags().create(new ConstTag(1, "title", 1, false));
        final Nyx nyx2 = new MemoryNyx();
        nyx2.tags().create(new ConstTag(1, "title2", 2, false));
        new NyxSync(nyx1, nyx2).sync();

        final Tag tag1 = nyx1.tags().all().get(0);
        final Tag tag2 = nyx2.tags().all().get(0);

        Assertions.assertEquals(tag1.title(), tag2.title());
    }

    /**
     * New jotTags will be sync to second one.
     */
    @Test
    void newJotTagsToSecond() {
        final Nyx nyx1 = new MemoryNyx();
        nyx1.jotTags().link(1, 2);
        final Nyx nyx2 = new MemoryNyx();
        new NyxSync(nyx1, nyx2).sync();

        List<JotTag> tags1 = nyx1.jotTags().all();
        List<JotTag> tags2 = nyx2.jotTags().all();
        Assertions.assertEquals(tags1.size(), tags2.size());
    }

    /**
     * Test if new JotTag at second one will be sync to first one.
     */
    @Test
    void newJotTagToFirst() {
        final Nyx nyx1 = new MemoryNyx();
        final Nyx nyx2 = new MemoryNyx();
        nyx2.jotTags().link(1, 2);
        new NyxSync(nyx1, nyx2).sync();

        final List<JotTag> tags1 = nyx1.jotTags().all();
        final List<JotTag> tags2 = nyx2.jotTags().all();
        Assertions.assertEquals(tags1.size(), tags2.size());
    }

    /**
     * Test if new version of jotTag will be sync to second one from first one.
     */
    @Test
    void updateJotTagByVersionToSecondOne() {
        final Nyx nyx1 = new MemoryNyx();
        nyx1.jotTags().link(1, 2);
        nyx1.jotTags().update(new ConstJotTag(1, 2, true, 0));
        final Nyx nyx2 = new MemoryNyx();
        nyx2.jotTags().link(1, 2);
        new NyxSync(nyx1, nyx2).sync();

        final JotTag tag1 = nyx1.jotTags().all().get(0);
        final JotTag tag2 = nyx2.jotTags().all().get(0);

        Assertions.assertEquals(tag1.deleted(), tag2.deleted());
        Assertions.assertTrue(tag2.deleted());
        Assertions.assertEquals(tag1.version(), tag2.version());
    }

    /**
     * Test if new version of jot will be sync to first one from second one.
     */
    @Test
    void updateJotTagByVersionToFirstOne() {
        final Nyx nyx1 = new MemoryNyx();
        nyx1.jotTags().link(1,2);
        final Nyx nyx2 = new MemoryNyx();
        nyx2.jotTags().link(1,2);
        nyx2.jotTags().update(new ConstJotTag(1, 2, true, 0));
        new NyxSync(nyx1, nyx2).sync();

        final JotTag tag1 = nyx1.jotTags().all().get(0);
        final JotTag tag2 = nyx2.jotTags().all().get(0);

        Assertions.assertTrue(tag1.deleted());
        Assertions.assertEquals(tag1.version(), tag2.version());
    }
}