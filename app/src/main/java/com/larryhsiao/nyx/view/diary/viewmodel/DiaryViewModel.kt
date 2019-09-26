package com.larryhsiao.nyx.view.diary.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.larryhsiao.nyx.database.RDatabase
import com.larryhsiao.nyx.diary.*
import com.larryhsiao.nyx.diary.room.DiaryEntity
import com.larryhsiao.nyx.diary.room.RDiary
import com.larryhsiao.nyx.media.NewMedias
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/**
 * View Model for single diary.
 */
class DiaryViewModel(private val app: Application) : AndroidViewModel(app) {
    private val db = RDatabase.Singleton(app).value()
    private val diary = MutableLiveData<Diary>().apply {
        value = PhantomDiary()
    }

    override fun onCleared() {
        super.onCleared()
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
            // @todo #1 merge media entity operation code into same object(maybe UpdateDiary)
            UpdateDiary(
                db.diaryDao(),
                id,
                message,
                timestamp
            ).fire()
            db.mediaDao().deleteByDiaryId(id)
            diary.postValue(
                RoomDiary(
                    RDiary(
                        DiaryEntity(id, message, timestamp),
                        NewMedias(
                            app,
                            db.mediaDao(),
                            id,
                            mediaUri
                        ).value()
                    )
                )
            )
        }
    }

    /**
     * Delete the diary present by this view model
     */
    fun delete(): LiveData<Boolean> {
        val result = MutableLiveData<Boolean>().also { it.value = false }
        GlobalScope.launch {
            diary.value?.also {
                DiaryDeletion(
                    db,
                    it.id()
                ).fire()
            }
            diary.postValue(PhantomDiary())
            result.postValue(true)
        }
        return result
    }
}