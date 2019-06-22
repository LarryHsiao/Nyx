package com.larryhsiao.nyx.diary.pages

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import com.larryhsiao.nyx.R
import com.larryhsiao.nyx.diary.viewmodel.CalendarViewModel
import com.silverhetch.aura.view.activity.TransparentSource
import kotlinx.android.synthetic.main.activity_new_diary.*

/**
 * Create Diary Activity with Transparent background.
 */
class NewDiaryActivity : AppCompatActivity() {
    private lateinit var viewModel: CalendarViewModel
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_diary)

        viewModel = ViewModelProviders.of(this).get(CalendarViewModel::class.java)

        newDiary_saveButton.setOnClickListener {
            viewModel.newDiary(newDiary_newDiaryContent.text.toString())
                finish()
        }
    }
}