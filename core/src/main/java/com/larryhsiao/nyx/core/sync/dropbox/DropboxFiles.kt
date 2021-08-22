package com.larryhsiao.nyx.core.sync.dropbox

import com.larryhsiao.nyx.core.sync.RemoteFiles
import java.io.InputStream

class DropboxFiles : RemoteFiles {
    override fun get(path: String?): InputStream {
        TODO("Not yet implemented")
    }

    override fun post(path: String?, inputStream: InputStream?) {
        TODO("Not yet implemented")
    }

    override fun delete(path: String?) {
        TODO("Not yet implemented")
    }
}