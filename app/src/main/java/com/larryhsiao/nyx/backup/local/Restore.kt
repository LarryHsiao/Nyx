package com.larryhsiao.nyx.backup.local

import android.util.LongSparseArray
import com.google.gson.Gson
import com.larryhsiao.nyx.database.RDatabase
import com.larryhsiao.nyx.diary.room.DiaryEntity
import com.larryhsiao.nyx.media.room.MediaEntity
import com.larryhsiao.nyx.tag.room.TagDiaryEntity
import com.larryhsiao.nyx.tag.room.TagEntity
import com.larryhsiao.nyx.weather.room.WeatherEntity
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
        private const val TAG_JSON = "tag.json"
        private const val TAG_DIARY_JSON = "tag_diary.json"
        private const val WEATHER_JSON = "weather.json"
    }

    private val newDiaryIdMapping = LongSparseArray<Long>()
    private val newTagIdMapping = LongSparseArray<Long>()

    override fun fire() {
        restoreDiary()
        restoreMedia()
        restoreTag()
        restoreTagDiary()
        restoreWeather()
    }

    private fun restoreWeather() {
        Gson().fromJson(
            FileText(File(backupInstanceDir, WEATHER_JSON)).value(),
            Array<WeatherEntity>::class.java
        ).forEach { entity ->
            db.weatherDao().create(
                WeatherEntity(
                    0,
                    entity.iconUrl,
                    entity.raw
                )
            )
        }
    }

    private fun restoreDiary() {
        Gson().fromJson(
            FileText(File(backupInstanceDir, DIARY_JSON)).value(),
            Array<DiaryEntity>::class.java
        ).forEach { entity ->
            db.diaryDao().create(
                DiaryEntity(
                    0,
                    entity.title,
                    entity.timestamp
                )
            ).also { newId ->
                newDiaryIdMapping.put(entity.id, newId)
            }
        }
    }

    private fun restoreMedia() {
        Gson().fromJson(
            FileText(File(backupInstanceDir, MEDIA_JSON)).value(),
            Array<ExportedMedia>::class.java
        ).forEach {
            newDiaryIdMapping[it.media.diaryId]?.also { diaryId ->
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

    private fun restoreTag() {
        Gson().fromJson(
            FileText(File(backupInstanceDir, TAG_JSON)).value(),
            Array<TagEntity>::class.java
        ).forEach {
            db.tagDao().queryOrCreate(it.title).also { entity ->
                newTagIdMapping.put(it.id, entity.id)
            }
        }
    }

    private fun restoreTagDiary() {
        Gson().fromJson(
            FileText(File(backupInstanceDir, TAG_DIARY_JSON)).value(),
            Array<TagDiaryEntity>::class.java
        ).forEach {
            db.tagDiaryDao().create(
                TagDiaryEntity(
                    0,
                    newDiaryIdMapping[it.diaryId],
                    newTagIdMapping[it.tagId]
                )
            )
        }
    }
}