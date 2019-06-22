package com.larryhsiao.nyx.diary.pages

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.larryhsiao.nyx.R
import com.silverhetch.aura.view.activity.TransparentSource

/**
 * Create Diary Activity with Transparent background.
 */
class NewDiaryActivity : AppCompatActivity() {
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_diary)
    }
}