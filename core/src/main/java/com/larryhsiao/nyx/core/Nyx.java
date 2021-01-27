package com.larryhsiao.nyx.core;

import com.larryhsiao.nyx.core.attachments.Attachments;
import com.larryhsiao.nyx.core.attachments.file.NyxFiles;
import com.larryhsiao.nyx.core.jots.Jots;

public interface Nyx {
    Jots jots();

    Attachments attachments();

    NyxFiles files();

    // @todo #101 Metadata
    // @todo #102 Files
}
