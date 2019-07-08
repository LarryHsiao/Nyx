package com.larryhsiao.nyx.diary.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.larryhsiao.nyx.RDatabase
import com.larryhsiao.nyx.diary.*
import com.larryhsiao.nyx.diary.room.DiaryEntity
import com.larryhsiao.nyx.diary.room.RDiary
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/**
 * View Model for single diary.
 */
class DiaryViewModel(app: Application) : AndroidViewModel(app) {
    private val db = RDatabase.Factory(app).value()
    private val diary = MutableLiveData<Diary>().apply {
        value = PhantomDiary()
    }

    override fun onCleared() {
        super.onCleared()
        db.close()
    }

    /**
     * The diary live data.
     */
    fun diary(): LiveData<Diary> {
        return diary
    }

    /**
     * Load up this view model with given diary id.
     */
    fun loadUp(id: Long) {
        GlobalScope.launch {
            diary.postValue(
                DiaryById(
                    db.diaryDao(),
                    id
                ).value()
            )
        }
    }

    /**
     * Update the diary present by this view model
     */
    fun update(
        message: String,
        timestamp: Long,
        mediaUri: List<String>
    ) {
        GlobalScope.launch {
            val id = diary.value?.id() ?: -1
            UpdateDiary(
                db.diaryDao(),
                id,
                message,
                timestamp
            ).fire()
            diary.postValue(
                RoomDiary(
                    RDiary(
                        DiaryEntity(id, message, timestamp),
                        listOf()
                    )
                )
            )
            // @todo #feature-3 update diary with media.
        }

    }

    /**
     * Delete the diary present by this view model
     */
    fun delete(): LiveData<Boolean> {
        // @todo #feature-4 delete diary with clear media files
        val result = MutableLiveData<Boolean>().also { it.value = false }
        GlobalScope.launch {
            diary.value?.also {
                DiaryDeletion(
                    db.diaryDao(),
                    id = it.id()
                ).fire()
            }
            diary.postValue(PhantomDiary())
            result.postValue(true)
        }
        return result
    }
}