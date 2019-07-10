package com.larryhsiao.nyx.view.diary

import android.os.Bundle
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.FrameLayout
import androidx.core.view.ViewCompat
import com.silverhetch.aura.AuraActivity

/**
 * Create Diary Activity with Transparent background.
 */
class NewDiaryActivity : AuraActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(FrameLayout(this).apply {
            id = ViewCompat.generateViewId()
            layoutParams = ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT)
            setupPageControl(id)
        })

        rootPage(NewDiaryFragment())
    }
}