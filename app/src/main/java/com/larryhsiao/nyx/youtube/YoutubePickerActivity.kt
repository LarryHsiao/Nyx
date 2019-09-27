package com.larryhsiao.nyx.youtube

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.larryhsiao.nyx.R
import com.silverhetch.aura.AuraActivity
import kotlinx.android.synthetic.main.activity_youtube_picker.*

/**
 * Activity to pick a Youtube video by searching with Youtube Data API.
 *
 * Returns the youtube uri.
 */
class YoutubePickerActivity : AuraActivity() {
    private lateinit var viewModel: YoutubeSearchViewModel
    private val adapter = VideoAdapter { video ->
        setResult(
            Activity.RESULT_OK,
            Intent().also { it.data = Uri.parse(video.id()) })
        finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_youtube_picker)
        listView.layoutManager = LinearLayoutManager(this)
        listView.adapter = adapter
        viewModel = ViewModelProviders.of(this)
            .get(YoutubeSearchViewModel::class.java)
        viewModel.data().observe(this, Observer { adapter.loadUp(it) })
        searchButton.setOnClickListener {
            viewModel.search(editText.text.toString())
        }
        viewModel.error().observe(this, Observer {
            AlertDialog.Builder(listView.context)
                .setTitle(R.string.error)
                .setMessage(R.string.fetching_failed)
                .setPositiveButton(android.R.string.ok) { _: DialogInterface, i: Int ->
                    finish()
                }
                .show()
        })
    }
}