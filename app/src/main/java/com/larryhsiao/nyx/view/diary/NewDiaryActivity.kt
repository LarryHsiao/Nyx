package com.larryhsiao.nyx.view.diary

import android.os.Bundle
import android.view.Gravity
import android.view.Gravity.*
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.FrameLayout
import androidx.core.view.ViewCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.silverhetch.aura.AuraActivity
import com.silverhetch.aura.view.measures.DP

/**
 * Create Diary Activity with Transparent background.
 */
open class NewDiaryActivity : AuraActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(FrameLayout(this).apply {
            id = ViewCompat.generateViewId()
            layoutParams = ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT)
            setupPageControl(id)
            setupFabControl(FloatingActionButton(context).also { fab ->
                fab.layoutParams =
                    FrameLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT).also {
                        it.gravity = END or BOTTOM
                        it.marginEnd = DP(context, 32f).px().toInt()
                        it.bottomMargin = DP(context, 32f).px().toInt()
                    }
                fab.size = FloatingActionButton.SIZE_AUTO
                addView(fab)
            })
        })



        rootPage(NewDiaryFragment())
    }
}