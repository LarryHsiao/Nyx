package com.larryhsiao.nyx.view.diary.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.larryhsiao.nyx.RDatabase
import com.larryhsiao.nyx.diary.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*

/**
 * View model for presenting the calendar events
 */
class CalendarViewModel(app: Application) : AndroidViewModel(app) {
    private val db = RDatabase.Factory(app).value()

    /**
     * Create new diary
     */
    fun newDiary(
        title: String,
        timestamp: Long = Calendar.getInstance(TimeZone.getTimeZone("UTC")).timeInMillis
    ): LiveData<Diary> {
        val result = MutableLiveData<Diary>()
        GlobalScope.launch {
            NewDiary(
                db.diaryDao(),
                title,
                timestamp
            ).value().also { newDiary ->
                result.postValue(newDiary)
            }
        }
        return result
    }

    /**
     * Update exist diary.
     */
    fun updateDiary(
        diary: Diary,
        title: String,
        timestamp: Long
    ): LiveData<Diary> {
        val result = MutableLiveData<Diary>()
        GlobalScope.launch {
            UpdateDiary(
                db.diaryDao(),
                diary.id(),
                title,
                timestamp
            ).fire()
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
     * All diaries by date.
     */
    fun byDate(dateTimestamp: Long): LiveData<List<Diary>> {
        return MutableLiveData<List<Diary>>().apply {
            GlobalScope.launch {
                postValue(
                    DiaryByDate(
                        db.diaryDao(),
                        dateTimestamp
                    ).value()
                )
            }
        }
    }

    /**
     * Diary by ID
     */
    fun byId(id: Long): LiveData<Diary> {
        val result = MutableLiveData<Diary>()
        GlobalScope.launch {
            result.postValue(
                DiaryById(db.diaryDao(), id).value()
            )
        }
        return result
    }

    override fun onCleared() {
        super.onCleared()
        db.close()
    }
}