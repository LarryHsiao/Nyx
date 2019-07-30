package com.larryhsiao.nyx.backup.local

import android.util.LongSparseArray
import com.google.gson.Gson
import com.larryhsiao.nyx.database.RDatabase
import com.larryhsiao.nyx.diary.room.DiaryEntity
import com.larryhsiao.nyx.media.room.MediaEntity
import com.silverhetch.clotho.Action
import com.silverhetch.clotho.file.FileText
import com.silverhetch.clotho.file.ToFile
import java.io.File

/**
 * Restore jotted from given backup directory. All exist data remains there.
 */
class Restore(
    private val backupInstanceDir: File,
    private val mediaRoot: File,
    private val db: RDatabase
) : Action {
    companion object {
        private const val DIARY_JSON = "diary.json"
        private const val MEDIA_JSON = "media.json"
    }

    private val diaryIdMapping = LongSparseArray<Long>()

    override fun fire() {
        restoreDiary()
        restoreMedia()
    }

    private fun restoreDiary() {
        Gson().fromJson(
            FileText(File(backupInstanceDir, DIARY_JSON)).value(),
            Array<DiaryEntity>::class.java
        ).forEach {
            db.diaryDao().create(
                DiaryEntity(
                    0,
                    it.title,
                    it.timestamp
                )
            ).also { newId ->
                diaryIdMapping.put(it.id, newId)
            }
        }
    }

    private fun restoreMedia() {
        Gson().fromJson(
            FileText(File(backupInstanceDir, MEDIA_JSON)).value(),
            Array<ExportedMedia>::class.java
        ).forEach {
            diaryIdMapping[it.media.diaryId]?.also { diaryId ->
                if (it.exportedFileName.isEmpty()) {
                    db.mediaDao().create(
                        MediaEntity(0, diaryId, it.media.uri)
                    )
                    return
                }
                val mediaFile = File(
                    mediaRoot,
                    it.exportedFileName
                ).apply { createNewFile() }
                ToFile(
                    File(backupInstanceDir, it.exportedFileName),
                    mediaFile
                ) {}.fire()
                db.mediaDao().create(
                    MediaEntity(
                        0,
                        diaryId,
                        mediaFile.toURI().toASCIIString()
                    )
                )
            }
        }
    }
}