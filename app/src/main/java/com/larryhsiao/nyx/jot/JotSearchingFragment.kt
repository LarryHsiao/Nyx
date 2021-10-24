package com.larryhsiao.nyx.jot

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.datepicker.MaterialDatePicker
import com.larryhsiao.clotho.date.DateCalendar
import com.larryhsiao.clotho.date.DateEndCalendar
import com.larryhsiao.nyx.NyxFragment
import com.larryhsiao.nyx.R
import com.larryhsiao.nyx.ViewModelFactory
import com.larryhsiao.nyx.core.jots.Jot
import com.larryhsiao.nyx.utils.DateRangeText

/**
 * Fragment for search jot by keyword.
 */
class JotSearchingFragment : NyxFragment() {
    private val viewModel by lazy {
        ViewModelProvider(this, ViewModelFactory(app)).get(JotsSearchingViewModel::class.java)
    }

    private val adapter by lazy {
        JotsAdapter(app.nyx(), lifecycleScope) {
            PreferJotAction(this, it, ::toJotFragment).fire()
        }
    }

    private fun toJotFragment(jot: Jot) {
        findNavController().navigate(
            JotSearchingFragmentDirections.actionJotSearchingFragmentToJotFragment(jot.id())
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_jots_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<RecyclerView>(R.id.jotsSearching_jotsRecyclerView).adapter = adapter
        view.findViewById<EditText>(R.id.jotsSearching_searchKeyword).doAfterTextChanged {
            viewModel.preferJots(it?.toString() ?: "")
        }
        viewModel.jots().observe(viewLifecycleOwner, ::loadJots)
        viewModel.dateRange().observe(viewLifecycleOwner) { setupDateRangeText(it, view) }
        setupDateRangeText(Pair(Long.MIN_VALUE, Long.MAX_VALUE), view)
    }

    private fun loadJots(jots: List<Jot>) {
        requireView().findViewById<View>(R.id.jotsSearching_emptyIcon).isVisible = jots.isEmpty()
        adapter.load(jots)
    }

    override fun onResume() {
        super.onResume()
        viewModel.reload()
    }

    private fun setupDateRangeText(it: Pair<Long, Long>, view: View) {
        val dateRangeText = view.findViewById<TextView>(R.id.dateRangeIndicator_dateRangeText)
        val dateRangeIcon = view.findViewById<ImageView>(R.id.dateRangeIndicator_dateRangeIcon)
        if (it.second == Long.MAX_VALUE && it.first == Long.MIN_VALUE) {
            dateRangeText.text = ""
            dateRangeText.visibility = View.GONE
            dateRangeIcon.setImageResource(R.drawable.ic_calendar)
            dateRangeIcon.setOnClickListener { showDatePicker() }
        } else {
            dateRangeText.text = DateRangeText(
                DateCalendar(it.first),
                DateEndCalendar(it.second)
            ).value()
            dateRangeText.visibility = View.VISIBLE
            dateRangeIcon.setImageResource(R.drawable.ic_cross)
            dateRangeIcon.setOnClickListener { viewModel.clearDateRange() }
        }
        dateRangeText.setOnClickListener { showDatePicker() }
    }

    private fun showDatePicker() {
        val datePicker = MaterialDatePicker.Builder.dateRangePicker()
            .setTitleText(R.string.Select_date_range)
            .setSelection(
                androidx.core.util.Pair(
                    MaterialDatePicker.todayInUtcMilliseconds(),
                    MaterialDatePicker.todayInUtcMilliseconds()
                )
            )
            .build()
        datePicker.addOnPositiveButtonClickListener {
            viewModel.preferDateRange(it.first, it.second)
        }
        datePicker.show(childFragmentManager, null)
    }
}