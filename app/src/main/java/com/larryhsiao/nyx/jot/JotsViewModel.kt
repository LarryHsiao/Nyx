package com.larryhsiao.nyx.jot

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.larryhsiao.nyx.core.jots.AllJots
import com.larryhsiao.nyx.core.jots.Jot
import com.larryhsiao.nyx.core.jots.QueriedJots
import com.silverhetch.clotho.Source
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.sql.Connection

/**
 * ViewModel to represent Jot list.
 */
class JotsViewModel(private val db: Source<Connection>) : ViewModel() {
    private val jots = MutableLiveData<List<Jot>>()
    fun jots(): LiveData<List<Jot>> = jots

    fun fetch() = viewModelScope.launch {
        withContext(IO) {
            jots.postValue(QueriedJots(AllJots(db)).value())
        }
    }
}