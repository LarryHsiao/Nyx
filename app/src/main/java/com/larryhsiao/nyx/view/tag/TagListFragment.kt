package com.larryhsiao.nyx.view.tag

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.silverhetch.aura.AuraFragment
import com.larryhsiao.nyx.R
import com.larryhsiao.nyx.view.tag.viewmodel.TagListVM
import kotlinx.android.synthetic.main.fragment_tag_list.*

/**
 * Fragment of Tag list.
 */
class TagListFragment : AuraFragment() {
    private lateinit var vm: TagListVM
    private lateinit var adapter:TagAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_tag_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = TagAdapter()
        vm = ViewModelProviders.of(this).get(TagListVM::class.java)
        tagList_listView.layoutManager = LinearLayoutManager(view.context)
        tagList_listView.adapter = adapter
        tagList_createInput.setOnEditorActionListener { v, actionId, event ->
            when (actionId) {
                EditorInfo.IME_ACTION_DONE -> {
                    vm.createTag(tagList_createInput.text.toString())
                        .observe(this, Observer {
                            adapter.addTag(it)
                            tagList_listView.scrollToPosition(0)
                        })
                    true
                }
                else -> {
                    false
                }
            }
        }
        vm.tags().observe(this, Observer {
            adapter.load(it)
            tagList_listView.scrollToPosition(0)
        })
        vm.loadUpTags()
    }

}