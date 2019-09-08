package com.larryhsiao.nyx.view.tag

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo.*
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.silverhetch.aura.AuraFragment
import com.larryhsiao.nyx.R
import com.larryhsiao.nyx.view.diary.DiaryListFragment
import com.larryhsiao.nyx.view.tag.viewmodel.TagListVM
import kotlinx.android.synthetic.main.fragment_tag_list.*

/**
 * Fragment of Tag list.
 */
class TagListFragment : AuraFragment(), TextWatcher {


    private lateinit var vm: TagListVM
    private lateinit var adapter: TagAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_tag_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = TagAdapter() {
            nextPage(DiaryListFragment.newInstance(tagId = it.id()))
        }
        vm = ViewModelProviders.of(this).get(TagListVM::class.java)
        tagList_listView.layoutManager = LinearLayoutManager(view.context)
        tagList_listView.adapter = adapter
        tagList_searchInput.setOnEditorActionListener { v, actionId, event ->
            when (actionId) {
                IME_ACTION_SEARCH -> {
                    searchByInput()
                    true
                }
                else -> false
            }
        }
        tagList_searchInput.addTextChangedListener(this)
        vm.tags().observe(this, Observer {
            adapter.load(it)
            tagList_listView.scrollToPosition(0)
        })
        vm.loadUpTags()
    }

    private fun searchByInput() {
        vm.loadUpTags(tagList_searchInput.text.toString())
    }
    override fun afterTextChanged(s: Editable?) {
        searchByInput()
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
    }
}