package com.larryhsiao.nyx.web;

import com.larryhsiao.nyx.core.web.ClassResourceFiles;
import com.larryhsiao.nyx.core.web.TakeWebAccess;
import com.larryhsiao.nyx.core.web.WebAccess;
import org.junit.jupiter.api.Test;

/**
 * WebAccess tests
 */
public class WebAccessTest {

    /**
     * Check take access available.
     */
    @Test
    void availability() {
        WebAccess webAccess = new TakeWebAccess(
            new ClassResourceFiles()
        );
        webAccess.start();
    }
}
