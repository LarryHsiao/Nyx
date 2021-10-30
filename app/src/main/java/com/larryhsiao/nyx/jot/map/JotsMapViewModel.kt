package com.larryhsiao.nyx.jot.map

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

class JotsMapViewModel(private val app: NyxApplication) : ViewModel() {
    private val jotsLiveData = MutableLiveData<List<Jot>>()
    private val dateRange = MutableLiveData<Pair<Long, Long>>()

    fun jotsLiveData(): LiveData<List<Jot>> {
        return jotsLiveData
    }

    fun dateRangeLiveData(): LiveData<Pair<Long, Long>> {
        return dateRange
    }

    fun selectDateRange(from: Long, to: Long) {
        dateRange.value = Pair(from, to)
        loadJots()
    }

    fun clearDateRange() {
        dateRange.value = Pair(0, 0)
        loadJots()
    }

    fun loadJots() = viewModelScope.launch(IO) {
        val dateRange = dateRange.value ?: Pair(0L, 0L)
        jotsLiveData.postValue(
            if (dateRange.first == 0L && dateRange.second == 0L) {
                app.nyx().jots().all().filter { !it.deleted() }
            } else {
                app.nyx().jots().byDateRange(
                    DateCalendar(dateRange.first).value(),
                    DateEndCalendar(dateRange.second).value()
                )
            }
        )
    }
}