package com.larryhsiao.nyx

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.larryhsiao.nyx.diary.AllDiary
import com.larryhsiao.nyx.diary.Diary
import com.larryhsiao.nyx.diary.NewDiary
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList

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

    fun newDiary(title: String) {
        GlobalScope.launch {
            diary.postValue(diary.value?.toMutableList()?.also {
                it.add(
                    NewDiary(
                        db.diaryDao(),
                        title,
                        Calendar.getInstance(TimeZone.getTimeZone("GMT+0")).timeInMillis
                    ).value()
                )
            })
        }
    }

    fun events(): LiveData<List<Diary>> {
        return diary
    }

    override fun onCleared() {
        super.onCleared()
        db.close()
    }
}