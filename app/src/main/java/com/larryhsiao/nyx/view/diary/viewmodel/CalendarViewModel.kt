package com.larryhsiao.nyx.view.diary.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.larryhsiao.nyx.database.RDatabase
import com.larryhsiao.nyx.diary.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.HashMap

/**
 * View model for presenting the calendar events
 */
class CalendarViewModel(private val app: Application) : AndroidViewModel(app) {
    private val db = RDatabase.Singleton(app).value()

    /**
     * Create new diary
     */
    fun newDiary(
        title: String,
        timestamp: Long = Calendar.getInstance(TimeZone.getTimeZone("UTC")).timeInMillis,
        mediaUris: List<String>
    ): LiveData<Diary> {
        val result = MutableLiveData<Diary>()
        GlobalScope.launch {
            NewDiary(
                app,
                db,
                title,
                timestamp,
                mediaUris
            ).value().also { newDiary ->
                result.postValue(newDiary)
            }
        }
        return result
    }

    /**
     * All diaries
     */
    fun diaries(): LiveData<List<Diary>> {
        return MutableLiveData<List<Diary>>().apply {
            GlobalScope.launch {
                postValue(AllDiary(db.diaryDao()).value())
            }
        }
    }

    /**
     * All diaries by given parameters.
     */
    fun diaries(
        dateTimestamp: Long = -1,
        tagId: Long = -1
    ): LiveData<List<Diary>> {
        return MutableLiveData<List<Diary>>().apply {
            GlobalScope.launch {
                postValue(
                    if (dateTimestamp != -1L && tagId != -1L) {
                        DiaryByFilteredDate(
                            DiaryByTag(db, tagId),
                            dateTimestamp
                        ).value()
                    } else if (tagId != -1L) {
                        DiaryByTag(db, tagId).value()
                    } else if (dateTimestamp != -1L) {
                        DiaryByDate(db.diaryDao(), dateTimestamp).value()
                    } else {
                        AllDiary(db.diaryDao()).value()
                    }
                )
            }
        }
    }
}