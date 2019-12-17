package com.larryhsiao.nyx.diary.room

import androidx.room.Embedded
import androidx.room.Relation
import com.larryhsiao.nyx.media.room.MediaEntity
import com.larryhsiao.nyx.weather.room.WeatherEntity

/**
 * Diary query from room with relations.
 */
data class RDiary(
    @Embedded
    val diary: DiaryEntity,

    @Relation(
        entity = MediaEntity::class,
        parentColumn = "id",
        entityColumn = "diary_id"
    )
    val mediaEntities: List<MediaEntity>,

    @Relation(
        entity = WeatherEntity::class,
        parentColumn = "weather_id",
        entityColumn = "id"
    )
    val weather: List<WeatherEntity> = ArrayList()
)