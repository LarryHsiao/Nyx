package com.larryhsiao.nyx.diary.pages

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.larryhsiao.nyx.R
import com.larryhsiao.nyx.diary.Diary
import com.larryhsiao.nyx.diary.viewmodel.CalendarViewModel
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
            val title = newDiary_newDiaryContent.text.toString()
            if (title.isNotEmpty()) {
                viewModel.newDiary(title, ToUTCTimestamp(calendar.timeInMillis).value()).observe(this, Observer<Diary>{
                    finish()
                })
            } else {
                Toast.makeText(this, R.string.title_should_not_empty, Toast.LENGTH_SHORT).show()
            }
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

    private fun updateDateIndicator() {
        newDiary_date.text = SimpleDateFormat.getDateInstance().format(Date().apply { time = calendar.timeInMillis })
    }
}