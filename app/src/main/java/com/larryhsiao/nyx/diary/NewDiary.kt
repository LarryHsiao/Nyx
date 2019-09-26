package com.larryhsiao.nyx.diary

import android.content.Context
import com.larryhsiao.nyx.database.RDatabase
import com.larryhsiao.nyx.diary.room.DiaryEntity
import com.larryhsiao.nyx.diary.room.RDiary
import com.larryhsiao.nyx.media.NewMedias
import com.silverhetch.clotho.Source

/**
 * Create or update new diary.
 */
class NewDiary(
    private val context: Context,
    private val db: RDatabase,
    private val title: String,
    private val utcTimestamp: Long,
    private val mediaUri: List<String>
) : Source<Diary> {
    override fun value(): Diary {
        if (title.isBlank()) {
            throw IllegalArgumentException("The title should not be empty")
        }
        val newId = db.diaryDao().create(DiaryEntity(0, title, utcTimestamp))
        return RoomDiary(
            RDiary(
                DiaryEntity(newId, title, utcTimestamp),
                NewMedias(
                    context,
                    db.mediaDao(),
                    newId,
                    mediaUri
                ).value()
            )
        )
    }
}