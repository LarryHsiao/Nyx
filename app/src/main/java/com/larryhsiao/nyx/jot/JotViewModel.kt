package com.larryhsiao.nyx.jot

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.larryhsiao.nyx.core.jots.ConstJot
import com.larryhsiao.nyx.core.jots.Jot
import com.larryhsiao.nyx.core.jots.JotById
import com.larryhsiao.nyx.core.jots.PostedJot
import com.silverhetch.clotho.Source
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.sql.Connection

/**
 * ViewModel to representing a jot.
 */
class JotViewModel(private val db: Source<Connection>) : ViewModel() {
    private val jotLiveData = MutableLiveData<Jot>()
    fun jot(): LiveData<Jot> = jotLiveData

    private val isNewJotLiveData = MutableLiveData<Boolean>()
    fun isNewJot(): LiveData<Boolean> = isNewJotLiveData

    fun loadJot(id: Long) = viewModelScope.launch(IO) {
        if (id != -1L) {
            isNewJotLiveData.postValue(false)
            jotLiveData.postValue(JotById(id, db).value())
        } else {
            isNewJotLiveData.postValue(true)
            jotLiveData.postValue(ConstJot())
        }
    }

    suspend fun save(jot: Jot): Jot = withContext(IO) {
        PostedJot(db, jot).value()
    }
}