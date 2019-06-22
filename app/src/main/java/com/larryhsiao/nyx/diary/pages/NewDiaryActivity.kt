package com.larryhsiao.nyx.diary.pages

import android.app.DatePickerDialog
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import com.larryhsiao.nyx.R
import com.larryhsiao.nyx.diary.viewmodel.CalendarViewModel
import com.silverhetch.aura.view.activity.TransparentSource
import com.silverhetch.clotho.time.ToUTCTimestamp
import kotlinx.android.synthetic.main.activity_new_diary.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.Calendar.*

/**
 * Create Diary Activity with Transparent background.
 */
class NewDiaryActivity : AppCompatActivity() {
    private lateinit var viewModel: CalendarViewModel
    private var calendar = getInstance()
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_diary)

        viewModel = ViewModelProviders.of(this).get(CalendarViewModel::class.java)

        newDiary_saveButton.setOnClickListener {
            viewModel.newDiary(newDiary_newDiaryContent.text.toString(), ToUTCTimestamp(calendar.timeInMillis).value())
            finish()
        }

        newDiary_date.setOnClickListener {
            DatePickerDialog(it.context)
                .apply {
                    setOnDateSetListener { view, year, month, dayOfMonth ->
                        calendar.set(YEAR, year)
                        calendar.set(MONTH, month)
                        calendar.set(DAY_OF_MONTH, dayOfMonth)
                        updateDateIndicator()
                    }
                }.show()
        }
        updateDateIndicator()
    }

    private fun updateDateIndicator(){
        newDiary_date.text = SimpleDateFormat.getDateInstance().format(Date().apply { time = calendar.timeInMillis })
    }
}