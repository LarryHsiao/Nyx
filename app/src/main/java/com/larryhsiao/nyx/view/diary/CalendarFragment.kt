package com.larryhsiao.nyx.view.diary

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.applandeo.materialcalendarview.EventDay
import com.larryhsiao.nyx.R
import com.larryhsiao.nyx.diary.Diary
import com.larryhsiao.nyx.view.diary.viewmodel.CalendarViewModel
import com.silverhetch.aura.AuraFragment
import com.silverhetch.aura.view.TintDrawable
import com.silverhetch.aura.view.fab.FabBehavior
import kotlinx.android.synthetic.main.fragment_calendar.*
import java.util.*

/**
 * Diary calendar fragment.
 */
class CalendarFragment : AuraFragment(), FabBehavior {
    private lateinit var viewModel: CalendarViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_calendar, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel =
            ViewModelProviders.of(this).get(CalendarViewModel::class.java)

        calendar_calendarView.setOnDayClickListener {
            nextPage(
                EventListFragment.newInstance(it.calendar.timeInMillis)
            )
        }
    }

    override fun onResume() {
        super.onResume()
        updateEvents()


        attachFab(this)
    }

    override fun onPause() {
        super.onPause()
        detachFab()
    }

    private fun updateEvents() {
        viewModel.diaries().observe(this, Observer<List<Diary>> { diaries ->
            calendar_calendarView.setEvents(
                Array(diaries.size) {
                    eventDay(diaries[it])
                }.toList()
            )
        })

    }

    override fun onClick() {
        startActivity(Intent(context, NewDiaryActivity::class.java))
    }

    override fun icon(): Int {
        return R.drawable.ic_plus
    }

    private fun eventDay(diary: Diary): EventDay {
        return EventDay(
            Calendar.getInstance().also { calendar ->
                calendar.timeInMillis = diary.timestamp()
            },
            TintDrawable(
                resources.getDrawable(R.drawable.ic_dot),
                Color.GREEN
            ).value()
        )
    }
}