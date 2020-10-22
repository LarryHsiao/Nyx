package com.larryhsiao.nyx.jdk

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.io.File

class FileTest {
    /**
     * The file path should not changed after invoked renameTo.
     */
    @Test
    internal fun renameTo() {
        val file = File("/")
        file.renameTo(File("/temp"))
        Assertions.assertEquals(
            "/",
            file.absolutePath
        )
    }
}