package com.larryhsiao.nyx.diary.pages

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.larryhsiao.nyx.R
import com.larryhsiao.nyx.databinding.PageDiaryBinding
import com.larryhsiao.nyx.diary.Diary
import com.larryhsiao.nyx.diary.viewmodel.CalendarViewModel
import com.silverhetch.aura.AuraFragment
import com.silverhetch.aura.view.fab.FabBehavior
import kotlinx.android.synthetic.main.page_diary.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * Fragment to show the exist diary.
 */
class DiaryFragment : AuraFragment() {
    companion object {
        private const val ARG_ID = "ARG_ID"
        private const val ARG_EDITABLE = "ARG_EDITABLE"

        /**
         * Factory method
         */
        fun newInstance(id: Long, editable: Boolean = false): Fragment {
            return DiaryFragment().apply {
                arguments = Bundle().also {
                    it.putLong(ARG_ID, id)
                    it.putBoolean(ARG_EDITABLE, editable)
                }
            }
        }
    }

    private lateinit var viewModel: CalendarViewModel
    private lateinit var editable: MutableLiveData<Boolean>
    private var calendar: Calendar = Calendar.getInstance()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return DataBindingUtil.inflate<PageDiaryBinding>(inflater, R.layout.page_diary, container, false).root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        editable = MutableLiveData<Boolean>().also {
            it.value = arguments?.getBoolean(ARG_EDITABLE) ?: false
        }
        viewModel = ViewModelProviders.of(this).get(CalendarViewModel::class.java)
        viewModel.byId(arguments?.getLong(ARG_ID) ?: 0L).observe(this, Observer<Diary> { diary ->
            val binding = DataBindingUtil.findBinding<PageDiaryBinding>(view)
            binding?.diary = diary
            editable.observe(this, Observer<Boolean> {
                binding?.editable = it
                newDiary_imageGrid.addable(it)
                if (!it) {
                    editableFab()
                }
            })

            calendar.time = Date().also { it.time = diary.timestamp() }
            newDiary_saveButton.setOnClickListener {
                viewModel.updateDiary(
                    diary,
                    newDiary_newDiaryContent.text.toString(),
                    calendar.time.time
                )
                editable.value = false
            }
        })
        newDiary_date.setOnClickListener {
            DatePickerDialog(it.context)
                .apply {
                    setOnDateSetListener { view, year, month, dayOfMonth ->
                        calendar[Calendar.YEAR] = year
                        calendar[Calendar.MONTH] = month
                        calendar[Calendar.DAY_OF_MONTH] = dayOfMonth
                        updateDateIndicator()
                    }
                }.show()
        }
        newDiary_imageGrid.setCallback { index, isAddingButton ->
            if (isAddingButton) {
                TODO()
            } else {
                TODO()
            }
        }
    }


    private fun updateDateIndicator() {
        newDiary_date.text = SimpleDateFormat.getDateInstance().format(Date().apply { time = calendar.timeInMillis })
    }

    override fun onResume() {
        super.onResume()
    }

    private fun editableFab() {
        attachFab(object : FabBehavior {
            override fun onClick() {
                editable.value = true
                detachFab()
            }

            override fun icon(): Int {
                return R.drawable.ic_pencil
            }
        })
    }
}