package com.larryhsiao.nyx.jot

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.larryhsiao.nyx.core.jots.Jot
import com.larryhsiao.nyx.core.jots.JotById
import com.silverhetch.clotho.Source
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import java.sql.Connection

/**
 * ViewModel to representing a jot.
 */
class JotViewModel(private val db: Source<Connection>) : ViewModel() {
    private val jot = MutableLiveData<Jot>()
    fun jot(): LiveData<Jot> = jot

    fun loadJot(id: Long) = viewModelScope.launch(IO) {
        if (id != -1L){
            jot.postValue(JotById(id, db).value())
        }
    }
}