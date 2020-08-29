package com.larryhsiao.nyx.jot

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.larryhsiao.nyx.core.jots.Jot
import com.larryhsiao.nyx.core.jots.JotsByDate
import com.larryhsiao.nyx.core.jots.QueriedJots
import com.silverhetch.clotho.Source
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.sql.Connection
import java.sql.Date
import java.util.*

/**
 * ViewModel to represent Jot list.
 */
class JotsViewModel(private val db: Source<Connection>) : ViewModel() {
    private val selected = MutableLiveData<Calendar>().apply {
        value = Calendar.getInstance()
    }
    private val jots = MutableLiveData<List<Jot>>()
    private val loading = MutableLiveData<Boolean>()

    fun loading(): LiveData<Boolean> = loading

    fun selected(): LiveData<Calendar> = selected

    fun jots(): LiveData<List<Jot>> = jots

    fun byDate(calendar: Calendar) = viewModelScope.launch {
        withContext(IO) {
            loading.postValue(true)
            jots.postValue(
                QueriedJots(
                    JotsByDate(
                        Date(calendar.timeInMillis),
                        db
                    )
                ).value()
            )
            loading.postValue(false)
        }
    }
}