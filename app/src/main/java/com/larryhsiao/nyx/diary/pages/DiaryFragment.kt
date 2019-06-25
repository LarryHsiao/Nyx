package com.larryhsiao.nyx.diary.pages

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.larryhsiao.nyx.R
import com.larryhsiao.nyx.databinding.PageDiaryBinding
import com.larryhsiao.nyx.diary.Diary
import com.larryhsiao.nyx.diary.viewmodel.CalendarViewModel
import com.silverhetch.aura.AuraFragment

/**
 * Fragment to show the exist diary.
 */
class DiaryFragment : AuraFragment() {
    companion object {
        private const val ARG_ID = "ARG_ID"
        fun newInstance(id: Long): Fragment {
            return DiaryFragment().apply {
                arguments = Bundle().also {
                    it.putLong(ARG_ID, id)
                }
            }
        }
    }

    private lateinit var viewModel: CalendarViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return DataBindingUtil.inflate<PageDiaryBinding>(inflater, R.layout.page_diary, container, false).root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(CalendarViewModel::class.java)
        viewModel.byId(arguments?.getLong(ARG_ID) ?: 0L).observe(this, Observer<Diary> {
            DataBindingUtil.findBinding<PageDiaryBinding>(view)?.diary = it
        })
    }
}