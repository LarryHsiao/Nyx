package com.larryhsiao.nyx.azure

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.larryhsiao.nyx.BuildConfig
import com.larryhsiao.nyx.BuildConfig.AZURE_SECRET_KEY
import com.larryhsiao.nyx.azure.IssueToken
import com.larryhsiao.nyx.azure.translation.Translation
import com.silverhetch.clotho.Source
import com.silverhetch.clotho.source.CachedSource
import com.silverhetch.clotho.storage.MemoryCeres
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException

/**
 * Translate labels to system language.
 */
class TranslatedStrings(
    private val app: Context,
    private val labels: List<String>
) : Source<LiveData<List<String>>> {
    private val ceres = MemoryCeres()
    override fun value(): LiveData<List<String>> {
        val result = MutableLiveData<List<String>>().apply {
            value = ArrayList()
        }
        GlobalScope.launch {
            try {
                val translated = ArrayList<String>()
                Translation(
                    CachedSource(
                        "azure_token",
                        8 * 60 * 1000,
                        ceres,
                        IssueToken(AZURE_SECRET_KEY)
                    ),
                    labels.toTypedArray(),
                    app.resources.configuration.locale.toLanguageTag()
                ).value().apply { translated.addAll(this) }
                result.postValue(translated)
            } catch (e: IllegalArgumentException) {
                result.postValue(labels)
            }
        }
        return result
    }
}