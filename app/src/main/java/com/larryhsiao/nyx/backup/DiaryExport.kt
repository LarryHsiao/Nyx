package com.larryhsiao.nyx.backup

import com.google.gson.Gson
import com.larryhsiao.nyx.database.RDatabase
import com.silverhetch.clotho.Action
import java.io.File
import java.io.FileOutputStream

/**
 * Export all diary to target file.
 */
class DiaryExport(
    private val db: RDatabase,
    private val targetFile: File
) : Action {

    override fun fire() {
        val gson = Gson()

        FileOutputStream(targetFile).use { stream ->
            stream.write("[".toByteArray())
            db.diaryDao().all().forEachIndexed { index, rDiary ->
                if (index > 0) {
                    stream.write(",".toByteArray())
                }
                stream.write(gson.toJson(rDiary.diary).toByteArray())
            }
            stream.write("]".toByteArray())
        }
    }
}