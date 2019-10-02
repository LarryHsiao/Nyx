package com.larryhsiao.nyx.view.tag

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo.IME_ACTION_SEARCH
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.larryhsiao.nyx.R
import com.larryhsiao.nyx.view.diary.DiaryListFragment
import com.larryhsiao.nyx.view.tag.viewmodel.TagListVM
import com.silverhetch.aura.AuraFragment
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
        adapter = TagAdapter({
            nextPage(DiaryListFragment.newInstance(tagId = it.id()))
        }, { it, index ->
            AlertDialog.Builder(view.context)
                .setMessage(R.string.delete)
                .setPositiveButton(
                    R.string.confirm,
                    DialogInterface.OnClickListener { dialog, which ->
                        vm.deleteTag(it)
                        adapter.remove(index)
                    })
                .setNegativeButton(
                    R.string.cancel,
                    DialogInterface.OnClickListener { _, _ -> })
                .show()
        })
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

    override fun onResume() {
        super.onResume()
        setTitle(getString(R.string.tags))
    }

    private fun searchByInput() {
        vm.loadUpTags(tagList_searchInput.text.toString())
    }

    override fun afterTextChanged(s: Editable?) {
        searchByInput()
    }

    override fun beforeTextChanged(
        s: CharSequence?,
        start: Int,
        count: Int,
        after: Int
    ) {
        /* Leave it empty*/
    }

    override fun onTextChanged(
        s: CharSequence?,
        start: Int,
        before: Int,
        count: Int
    ) {
        /* Leave it empty*/
    }
}