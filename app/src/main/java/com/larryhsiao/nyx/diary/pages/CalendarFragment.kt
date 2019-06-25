package com.larryhsiao.nyx.diary.pages

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.applandeo.materialcalendarview.CalendarUtils
import com.applandeo.materialcalendarview.EventDay
import com.larryhsiao.nyx.R
import com.larryhsiao.nyx.diary.Diary
import com.larryhsiao.nyx.diary.viewmodel.CalendarViewModel
import com.silverhetch.aura.AuraFragment
import com.silverhetch.aura.view.dialog.InputDialog
import com.silverhetch.aura.view.fab.FabBehavior
import com.silverhetch.aura.view.measures.DP
import com.silverhetch.clotho.time.ToUTCTimestamp
import kotlinx.android.synthetic.main.fragment_calendar.*
import java.util.*

/**
 * Diary calendar fragment.
 */
class CalendarFragment : AuraFragment(), FabBehavior {
    companion object {
        private const val REQUEST_CODE_INPUT = 1000
    }

    private lateinit var viewModel: CalendarViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_calendar, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(CalendarViewModel::class.java)
        viewModel.diaries().observe(this, Observer<List<Diary>> {
            updateEvents()
        })

        calendar_calendarView.setOnDayClickListener {
            nextPage(EventListFragment.newInstance(it.calendar.timeInMillis))
        }
    }

    override fun onResume() {
        super.onResume()
        attachFab(this)
    }

    override fun onPause() {
        super.onPause()
        detachFab(this)
    }

    private fun updateEvents() {
        viewModel.diaries().value?.also { diaries ->
            calendar_calendarView.setEvents(
                Array(diaries.size) {
                    val title = diaries[it].title()
                    EventDay(
                        Calendar.getInstance().also { calendar ->
                            calendar.timeInMillis = diaries[it].timestamp()
                        },
                        CalendarUtils.getDrawableText(
                            context,
                            title,
                            Typeface.DEFAULT,
                            R.color.colorPrimaryDark,
                            10
                        )
                    )
                }.toList()
            )
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_INPUT && resultCode == Activity.RESULT_OK) {
            viewModel.newDiary(data?.getStringExtra("INPUT_FIELD") ?: "")
        }
    }

    override fun onClick() {
        startActivity(Intent(context, NewDiaryActivity::class.java))
    }

    override fun icon(): Int {
        return R.drawable.ic_plus
    }
}