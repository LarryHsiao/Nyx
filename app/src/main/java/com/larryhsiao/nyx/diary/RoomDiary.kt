package com.larryhsiao.nyx.diary

import android.net.Uri
import com.google.gson.JsonParser
import com.larryhsiao.nyx.diary.room.RDiary
import com.larryhsiao.nyx.weather.OpenWeatherWeather
import com.larryhsiao.nyx.weather.Weather
import java.util.*

/**
 * The [RDiary] adapter of diary.
 */
class RoomDiary(private val roomDiary: RDiary) : Diary {
    override fun id(): Long {
        return roomDiary.diary.id
    }

    override fun title(): String {
        return roomDiary.diary.title ?: ""
    }

    override fun timestamp(): Long {
        return Calendar.getInstance().also {
            it.timeZone = TimeZone.getTimeZone("UTC")
            it.timeInMillis = roomDiary.diary.timestamp ?: 0L
            it.timeZone = TimeZone.getDefault()
        }.timeInMillis
    }

    override fun attachmentUris(): Array<Uri> {
        val medias = roomDiary.mediaEntities
        return Array(medias.size) {
            Uri.parse(medias[it].uri)
        }
    }

    override fun weatherIconUrl(): String {
        return roomDiary.weather.takeIf { it.isNotEmpty() }?.let {
            OpenWeatherWeather(JsonParser().parse(it[0].raw)).iconUrl()
        } ?: ""
    }
}