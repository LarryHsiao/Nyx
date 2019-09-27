package com.larryhsiao.nyx.youtube

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.larryhsiao.nyx.R
import com.silverhetch.aura.view.ViewHolder
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_video.view.*

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
                R.layout.item_video,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.rootView.textView.text = items[position].title()
        holder.rootView.setOnClickListener {
            onClick(items[holder.adapterPosition])
        }
        Picasso.get()
            .load(Uri.parse(items[position].thumbnailUrl()))
            .placeholder(CircularProgressDrawable(holder.rootView.context).also {
                it.setStyle(CircularProgressDrawable.DEFAULT)
            })
            .into(holder.rootView.imageView)
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