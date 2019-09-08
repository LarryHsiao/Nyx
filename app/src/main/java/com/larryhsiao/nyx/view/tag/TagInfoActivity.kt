package com.larryhsiao.nyx.view.tag

import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.TextView
import com.larryhsiao.nyx.database.RDatabase
import com.larryhsiao.nyx.tag.TagById
import com.larryhsiao.nyx.tag.UriTagId
import com.silverhetch.aura.AuraActivity
import java.net.URI

/**
 * Activity to show information of tag
 */
class TagInfoActivity : AuraActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(TextView(this).apply {
            layoutParams = ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT)
            gravity = Gravity.CENTER

            text = tagInfo()
        })
    }

    private fun tagInfo(): String {
        return intent?.data?.let {
            val tag = TagById(
                RDatabase.Singleton(this).value(),
                UriTagId(URI.create(it.toString())).value()
            ).value()

            """ID: ${tag.id()}
                |Title: ${tag.title()}
            """.trimMargin()
        } ?: ""
    }
}