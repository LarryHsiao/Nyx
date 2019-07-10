package com.larryhsiao.nyx.view.diary

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.larryhsiao.nyx.R
import com.larryhsiao.nyx.diary.Diary
import com.larryhsiao.nyx.view.diary.viewmodel.CalendarViewModel
import com.silverhetch.aura.AuraFragment
import com.silverhetch.clotho.time.ToUTCTimestamp
import kotlinx.android.synthetic.main.page_diary.*
import kotlinx.android.synthetic.main.page_diary.view.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * Page for creating new diary.
 */
class NewDiaryFragment : AuraFragment() {
    private lateinit var viewModel: CalendarViewModel
    private var calendar = Calendar.getInstance()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.page_diary, container, false)
        viewModel = ViewModelProviders.of(this).get(CalendarViewModel::class.java)

        view.newDiary_saveButton.setOnClickListener {
            val title = newDiary_newDiaryContent.text.toString()
            if (title.isNotEmpty()) {
                viewModel.newDiary(title, ToUTCTimestamp(calendar.timeInMillis).value()).observe(this, Observer<Diary> {
                    activity?.onBackPressed()
                })
            } else {
                Toast.makeText(inflater.context, R.string.title_should_not_empty, Toast.LENGTH_SHORT).show()
            }
        }

        view.newDiary_date.setOnClickListener {
            DatePickerDialog(it.context)
                .apply {
                    setOnDateSetListener { view, year, month, dayOfMonth ->
                        calendar.set(Calendar.YEAR, year)
                        calendar.set(Calendar.MONTH, month)
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                        updateDateIndicator(view)
                    }
                }.show()
        }
        updateDateIndicator(view)
        return view
    }

    private fun updateDateIndicator(view: View) {
        view.newDiary_date.text =
            SimpleDateFormat.getDateInstance().format(Date().apply { time = calendar.timeInMillis })
    }
}