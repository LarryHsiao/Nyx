package com.larryhsiao.nyx.jot

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.larryhsiao.nyx.NyxApplication
import com.larryhsiao.nyx.core.jots.Jot
import com.larryhsiao.nyx.core.jots.JotsByKeyword
import com.larryhsiao.nyx.core.jots.QueriedJots
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * ViewModels for jots searching.
 */
class JotsSearchingViewModel(private val app: NyxApplication) : ViewModel() {
    private val keyword = MutableLiveData("_")
    private val jots = MutableLiveData<List<Jot>>()

    fun jots(): LiveData<List<Jot>> = jots

    fun keyword(): LiveData<String> = keyword

    fun preferJots(newKeyword: String) = viewModelScope.launch {
        if (keyword.value == newKeyword) {
            return@launch
        }
        keyword.value = newKeyword
        loadByKeyword()
    }

    fun reload() = viewModelScope.launch {
        loadByKeyword()
    }

    private suspend fun loadByKeyword(){
        val result: List<Jot> = withContext(IO) {
            QueriedJots(
                JotsByKeyword(
                    app.db,
                    keyword.value ?: ""
                )
            ).value()
        }
        jots.value = result
    }
}