package com.larryhsiao.nyx.view.diary

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.larryhsiao.nyx.R
import com.larryhsiao.nyx.view.diary.viewmodel.CalendarViewModel
import com.silverhetch.aura.AuraFragment
import com.silverhetch.aura.view.fab.FabBehavior

/**
 * Fragment shows all event list by dateTime
 */
class DiaryListFragment : AuraFragment(), FabBehavior {
    companion object {
        private const val ARG_DATETIME = "ARG_DATETIME"
        private const val ARG_TAG_ID = "ARG_TAG_ID"
        private const val ARG_DIARIES_ID = "ARG_DIARIES_ID"
        private const val REQUEST_CODE_NEW_DIARY = 1000
        private const val REQUEST_CODE_DIARY = 1001

        fun newInstance(ids: LongArray): Fragment {
            return DiaryListFragment().apply {
                arguments = Bundle().apply {
                    putLongArray(ARG_DIARIES_ID, ids)
                }
            }
        }

        /**
         * @param dateTime The specific date in timestamp
         */
        fun newInstance(dateTime: Long = -1L, tagId: Long = -1L): Fragment {
            return DiaryListFragment().apply {
                arguments = Bundle().apply {
                    putLong(ARG_DATETIME, dateTime)
                    putLong(ARG_TAG_ID, tagId)
                }
            }
        }
    }

    private val diariesId: LongArray by lazy {
        arguments?.getLongArray(ARG_DIARIES_ID) ?: LongArray(0)
    }

    private val dateTimestamp by lazy {
        arguments?.getLong(ARG_DATETIME, -1L) ?: -1L
    }

    private val tagId: Long by lazy {
        arguments?.getLong(ARG_TAG_ID, -1L) ?: -1L
    }

    private lateinit var viewModel: CalendarViewModel
    private lateinit var list: RecyclerView
    private val adapter = DiaryAdapter() {
        nextPage(
            DiaryFragment.newInstance(
                it.id()
            ).also { fragment ->
                fragment.setTargetFragment(
                    this,
                    REQUEST_CODE_DIARY
                )
            })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel =
            ViewModelProviders.of(this).get(CalendarViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return RecyclerView(inflater.context).also {
            list = it
            list.adapter = adapter
            it.layoutParams = ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT)
            it.layoutManager = LinearLayoutManager(inflater.context)
        }
    }

    override fun onResume() {
        super.onResume()
        attachFab(this)
    }

    override fun onPause() {
        super.onPause()
        detachFab()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadData()
    }

    override fun onClick() {
        startActivityForResult(
            Intent(context, NewDiaryActivity::class.java),
            REQUEST_CODE_NEW_DIARY
        )
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_NEW_DIARY) {
            loadData()
        }
        if (requestCode == REQUEST_CODE_DIARY) {
            activity?.onBackPressed()
            loadData()
        }
    }

    private fun loadData() {
        viewModel.title().observe(this, Observer {
            val activityRef = activity
            if (activityRef is AppCompatActivity)
                activityRef.supportActionBar?.title = it
        })
        viewModel.loadUp(
            dateTimestamp,
            tagId,
            diariesId
        ).observe(this, Observer {
            adapter.load(it)
        })
    }

    override fun icon(): Int {
        return R.drawable.ic_plus
    }
}