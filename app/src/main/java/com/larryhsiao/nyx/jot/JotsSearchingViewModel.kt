package com.larryhsiao.nyx.jot

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.larryhsiao.clotho.date.DateCalendar
import com.larryhsiao.clotho.date.DateEndCalendar
import com.larryhsiao.nyx.NyxApplication
import com.larryhsiao.nyx.core.jots.Jot
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * ViewModels for jots searching.
 */
class JotsSearchingViewModel(private val app: NyxApplication) : ViewModel() {
    private val keyword = MutableLiveData("")
    private val dateRange = MutableLiveData(Pair(Long.MIN_VALUE, Long.MAX_VALUE))
    private val jots = MutableLiveData<List<Jot>>()

    fun jots(): LiveData<List<Jot>> = jots

    fun keyword(): LiveData<String> = keyword

    fun dateRange(): LiveData<Pair<Long, Long>> = dateRange

    fun preferDateRange(start: Long, end: Long) = viewModelScope.launch {
        dateRange.value = Pair(
            DateCalendar(start).value().timeInMillis,
            DateEndCalendar(end).value().timeInMillis
        )
        loadJots()
    }

    fun clearDateRange() = viewModelScope.launch {
        dateRange.value = Pair(Long.MIN_VALUE, Long.MAX_VALUE)
        loadJots()
    }

    fun preferJots(newKeyword: String) = viewModelScope.launch {
        if (keyword.value == newKeyword) {
            return@launch
        }
        keyword.value = newKeyword
        loadJots()
    }

    fun reload() = viewModelScope.launch { loadJots() }

    private suspend fun loadJots() {
        val result: List<Jot> = withContext(IO) {
            app.nyx().jots().byKeyword(keyword.value ?: "")
        }
        val dateRange = dateRange.value ?: Pair(Long.MIN_VALUE, Long.MAX_VALUE)
        jots.value = result.filter {
            it.createdTime() >= dateRange.first && it.createdTime() <= dateRange.second
        }
    }
}