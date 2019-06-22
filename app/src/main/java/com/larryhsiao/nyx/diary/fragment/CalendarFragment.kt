package com.larryhsiao.nyx.diary.fragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.applandeo.materialcalendarview.EventDay
import com.larryhsiao.nyx.R
import com.larryhsiao.nyx.diary.Diary
import com.larryhsiao.nyx.diary.viewmodel.CalendarViewModel
import com.silverhetch.aura.AuraFragment
import com.silverhetch.aura.view.dialog.InputDialog
import com.silverhetch.aura.view.fab.FabBehavior
import com.silverhetch.clotho.time.ToUTCTimestamp
import kotlinx.android.synthetic.main.fragment_calendar.*
import java.util.*

class CalendarFragment : AuraFragment() {
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
        fabControl().attachFab(object : FabBehavior {
            override fun onClick() {
                InputDialog.newInstance(
                    getString(R.string.what_is_in_your_mind)
                ).also {
                    it.setTargetFragment(this@CalendarFragment,
                        REQUEST_CODE_INPUT
                    )
                }.show(requireFragmentManager(), null)
            }

            override fun icon(): Int {
                return R.drawable.ic_plus
            }
        })

        calendar_calendarView.setOnDayClickListener {
            viewModel.byDate(ToUTCTimestamp(it.calendar.timeInMillis).value()).observe(
                this@CalendarFragment,
                Observer<List<Diary>> {
                    Toast.makeText(context!!, "size ${it.size}", Toast.LENGTH_LONG).show()
                }
            )
        }
    }

    private fun updateEvents() {
        viewModel.diaries().value?.also { diaries ->
            calendar_calendarView.setEvents(
                Array(diaries.size) {
                    EventDay(Calendar.getInstance().also { calendar ->
                        calendar.timeInMillis = diaries[it].timestamp()
                    }, resources.getDrawable(R.drawable.ic_object))
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
}