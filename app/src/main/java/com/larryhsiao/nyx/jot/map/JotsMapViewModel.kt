package com.larryhsiao.nyx.jot.map

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.larryhsiao.nyx.NyxApplication
import com.larryhsiao.nyx.core.jots.Jot
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class JotsMapViewModel(private val app: NyxApplication) : ViewModel() {
    private val jotsLiveData = MutableLiveData<List<Jot>>()

    fun jotsLiveData(): LiveData<List<Jot>> {
        return jotsLiveData
    }

    fun loadJots() = viewModelScope.launch(IO) {
        jotsLiveData.postValue(
            app.nyx().jots().all()
        )
    }
}