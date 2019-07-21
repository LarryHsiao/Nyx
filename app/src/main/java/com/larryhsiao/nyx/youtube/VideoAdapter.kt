package com.larryhsiao.nyx.youtube

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.silverhetch.aura.view.ViewHolder
import com.larryhsiao.nyx.R

/**
 * RecyclerView adapter to show [Video].
 */
class VideoAdapter(
    private val onClick: (item: Video) -> Unit
) : RecyclerView.Adapter<ViewHolder>() {
    private val items = ArrayList<Video>()
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                android.R.layout.simple_list_item_1,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val root = holder.rootView
        if (root is TextView) {
            root.text = items[position].title()
            root.setOnClickListener {
                onClick(items[holder.adapterPosition])
            }
        }
    }

    /**
     * Load up the list of [Video]
     */
    fun loadUp(input: List<Video>) {
        items.clear()
        items.addAll(input)
        notifyDataSetChanged()
    }
}