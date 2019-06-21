package com.larryhsiao.nyx.diary.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.larryhsiao.nyx.RDatabase
import com.larryhsiao.nyx.diary.AllDiary
import com.larryhsiao.nyx.diary.Diary
import com.larryhsiao.nyx.diary.DiaryByDate
import com.larryhsiao.nyx.diary.NewDiary
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*

/**
 * View model for presenting the calendar events
 */
class CalendarViewModel(app: Application) : AndroidViewModel(app) {
    private val db = RDatabase.Factory(app).value()
    private val diary: MutableLiveData<List<Diary>> by lazy {
        MutableLiveData<List<Diary>>().apply {
            GlobalScope.launch {
                postValue(AllDiary(db.diaryDao()).value())
            }
        }
    }

    fun newDiary(title: String): LiveData<Diary> {
        val result = MutableLiveData<Diary>()
        GlobalScope.launch {
            diary.postValue(diary.value?.toMutableList()?.also {
                it.add(
                    NewDiary(
                        db.diaryDao(),
                        title,
                        Calendar.getInstance(TimeZone.getTimeZone("UTC")).timeInMillis
                    ).value().also { newDiary -> result.postValue(newDiary) }
                )
            })
        }
        return result
    }

    fun events(): LiveData<List<Diary>> {
        return diary
    }

    fun byDate(dateTimestamp: Long): LiveData<List<Diary>> {
        val result = MutableLiveData<List<Diary>>()
        GlobalScope.launch {
            result.postValue(
                DiaryByDate(
                    db.diaryDao(),
                    dateTimestamp
                ).value()
            )
        }
        return result
    }

    override fun onCleared() {
        super.onCleared()
        db.close()
    }
}