package com.larryhsiao.nyx.backup.local

import android.os.Environment
import com.silverhetch.clotho.Source
import java.io.File

/**
 * Source to build backup File.
 *
 * Note: This class requires Permissions for reading/writing External storage.
 */
class BackupRootSource : Source<File> {
    override fun value(): File {
        return File(Environment.getExternalStorageDirectory(), "jotted_backup")
    }
}