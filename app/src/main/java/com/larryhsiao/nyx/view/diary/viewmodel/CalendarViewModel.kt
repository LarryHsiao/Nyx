package com.larryhsiao.nyx.view.diary.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.larryhsiao.nyx.R
import com.larryhsiao.nyx.database.RDatabase
import com.larryhsiao.nyx.diary.*
import com.larryhsiao.nyx.tag.TagById
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.DateFormat
import java.util.*

/**
 * View model for presenting the calendar events
 */
class CalendarViewModel(private val app: Application) : AndroidViewModel(app) {
    private val db = RDatabase.Singleton(app).value()
    private val title = MutableLiveData<String>()

    /**
     * Title of this viewModel
     */
    fun title(): LiveData<String> {
        return title
    }

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
    fun loadUp(): LiveData<List<Diary>> {
        title.value = app.getString(R.string.diaries)
        return MutableLiveData<List<Diary>>().apply {
            GlobalScope.launch {
                postValue(AllDiary(db.diaryDao()).value())
            }
        }
    }

    /**
     * All diaries by given parameters.
     */
    fun loadUp(
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

                title.postValue(
                    StringBuilder().apply {
                        if (tagId != -1L) {
                            append(TagById(db, tagId).value().title())
                        }

                        if (dateTimestamp != -1L) {
                            append(
                                DateFormat.getDateInstance().format(
                                    Date(dateTimestamp)
                                )
                            )
                        }

                        if (tagId == -1L && dateTimestamp == -1L) {
                            this.append(
                                app.getString(R.string.diaries)
                            )
                        }
                    }.toString()
                )
            }
        }
    }
}