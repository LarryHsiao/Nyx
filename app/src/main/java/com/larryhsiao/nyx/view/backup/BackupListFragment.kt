package com.larryhsiao.nyx.view.backup

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.silverhetch.aura.AuraFragment


/**
 * List of backup instance, currently available source: Google.
 */
class BackupListFragment : AuraFragment() {
    companion object {
        private const val REQUEST_CODE_SIGN_IN = 1000
    }

    private lateinit var list: RecyclerView
    private val adapter = BackupListAdapter()
    private lateinit var viewModel: BackupsViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this)
            .get(BackupsViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return RecyclerView(inflater.context).also {
            it.layoutParams = ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT)
            it.layoutManager = LinearLayoutManager(it.context)
            it.adapter = adapter
            list = it
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.backups().observe(this, Observer {
            adapter.loadUp(it)
        })

        viewModel.fetch()
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_SIGN_IN) {
        }
    }
}