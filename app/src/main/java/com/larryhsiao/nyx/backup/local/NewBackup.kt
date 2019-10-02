package com.larryhsiao.nyx.backup.local

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.larryhsiao.nyx.backup.DiaryExport
import com.larryhsiao.nyx.backup.MediaExport
import com.larryhsiao.nyx.backup.tag.TagDiaryExport
import com.larryhsiao.nyx.backup.tag.TagExport
import com.larryhsiao.nyx.database.RDatabase
import com.larryhsiao.nyx.diary.room.DiaryDao
import com.larryhsiao.nyx.media.room.MediaDao
import com.larryhsiao.nyx.tag.room.TagDao
import com.larryhsiao.nyx.tag.room.TagDiaryDao
import com.silverhetch.clotho.Action
import com.silverhetch.clotho.file.ToFile
import java.io.File
import java.io.FileOutputStream
import java.net.URI
import java.text.SimpleDateFormat
import java.util.*

/**
 * Create a new backup of Jotted to local storage.
 */
class NewBackup(
    private val db: RDatabase,
    private val rootDir: File
) : Action {
    override fun fire() {
        val backupRoot = File(
            rootDir,
            SimpleDateFormat("yyyyMMddHHmm", Locale.US).format(Date())
        ).also {
            it.mkdirs()
        }
        backupDiary(backupRoot, db.diaryDao())
        backupMedia(backupRoot, db.mediaDao())
        backupTags(backupRoot, db.tagDao())
        backupTagDiaries(backupRoot, db.tagDiaryDao())
    }

    private fun backupDiary(root: File, dao: DiaryDao) {
        FileOutputStream(File(root, "diary.json").also {
            it.createNewFile()
        }).use { output ->
            output.write("[".toByteArray())
            var counter = 0
            DiaryExport(dao).value().forEach {
                if (counter > 0) {
                    output.write(",".toByteArray())
                }
                output.write(it.json().toByteArray())
                counter++
            }
            output.write("]".toByteArray())
        }
    }

    private fun backupMedia(root: File, dao: MediaDao) {
        FileOutputStream(File(root, "media.json").also {
            it.createNewFile()
        }).use { output ->
            output.write("[".toByteArray())
            var counter = 0
            MediaExport(dao).value().forEach { original ->
                if (counter > 0) {
                    output.write(",".toByteArray())
                }

                var exportedFileName = ""
                if (original.mediaUri().scheme?.startsWith("file") == true) {
                    ToFile(
                        File(URI(original.mediaUri().toString())),
                        File(
                            root,
                            UUID.randomUUID().toString().substring(
                                0,
                                7
                            ) + original.hashCode()
                        ).also {
                            exportedFileName = it.name
                        }
                    ) {}.fire()
                }
                output.write(JsonObject().also {
                    it.add("media", JsonParser().parse(original.json()))
                    it.addProperty("exportedFileName", exportedFileName)
                }.toString().toByteArray())
                counter++
            }
            output.write("]".toByteArray())
        }
    }

    private fun backupTags(root: File, dao: TagDao) {
        FileOutputStream(File(root, "tag.json").also {
            it.createNewFile()
        }).use { output ->
            output.write("[".toByteArray())
            var counter = 0
            TagExport(dao).value().forEach {
                if (counter > 0) {
                    output.write(",".toByteArray())
                }
                output.write(it.toByteArray())
                counter++
            }
            output.write("]".toByteArray())
        }
    }

    private fun backupTagDiaries(root: File, dao: TagDiaryDao) {
        FileOutputStream(File(root, "tag_diary.json").also {
            it.createNewFile()
        }).use { output ->
            output.write("[".toByteArray())
            var counter = 0
            TagDiaryExport(dao).value().forEach {
                if (counter > 0) {
                    output.write(",".toByteArray())
                }
                output.write(it.toByteArray())
                counter++
            }
            output.write("]".toByteArray())
        }
    }
}