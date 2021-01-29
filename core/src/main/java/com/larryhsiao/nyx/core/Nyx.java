package com.larryhsiao.nyx.core;

import com.larryhsiao.nyx.core.attachments.Attachments;
import com.larryhsiao.nyx.core.attachments.file.NyxFiles;
import com.larryhsiao.nyx.core.jots.Jots;
import com.larryhsiao.nyx.core.metadata.MetadataSet;
import com.larryhsiao.nyx.core.tags.Tags;

public interface Nyx {
    Jots jots();

    Tags tags();

    Attachments attachments();

    NyxFiles files();

    MetadataSet metadataSet();
}
