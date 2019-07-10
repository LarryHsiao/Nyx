package com.larryhsiao.nyx.diary.room

import androidx.room.Embedded
import androidx.room.Relation
import com.larryhsiao.nyx.media.room.MediaEntity

/**
 * Diary query from room with relations.
 */
data class RDiary(
    @Embedded
    val diary: DiaryEntity,

    @Relation(entity = MediaEntity::class, parentColumn = "id", entityColumn = "diary_id")
    val mediaEntities: List<MediaEntity>
)