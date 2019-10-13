package com.larryhsiao.nyx.view.diary.viewmodel

import android.app.Application
import android.location.Location
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.silverhetch.aura.location.LocationAddress
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/**
 * ViewModel of Address
 */
class AddressViewModel(private val app: Application) : AndroidViewModel(app) {
    private val address = MutableLiveData<String>().apply { value = "" }

    /**
     * The address live data
     */
    fun address(): LiveData<String> {
        return address
    }

    /**
     * Load the addres by location uri.
     */
    fun load(uri: String) {
        GlobalScope.launch {
            LocationAddress(app, Location("uri_provider").apply {
                val segments = uri.replace("geo:", "")
                    .split(",")
                latitude = segments[0].toDouble()
                longitude = segments[1].toDouble()
            }).value().apply {
                address.postValue(getAddressLine(0))
            }
        }
    }
}