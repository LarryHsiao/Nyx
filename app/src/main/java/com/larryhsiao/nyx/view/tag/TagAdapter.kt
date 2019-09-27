package com.larryhsiao.nyx.view.tag

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.larryhsiao.nyx.R
import com.larryhsiao.nyx.tag.Tag
import com.silverhetch.aura.view.ViewHolder
import kotlinx.android.synthetic.main.item_tag.view.*

/**
 * RecyclerView adapter for tags
 */
class TagAdapter(
    private val tagClicked: (tag: Tag) -> Unit
) :
    RecyclerView.Adapter<ViewHolder>() {
    private val tags = ArrayList<Tag>()
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_tag,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return tags.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemView.tag_title.text = tags[position].title()
        holder.itemView.setOnClickListener { tagClicked(tags[holder.adapterPosition]) }
    }

    /**
     * load list
     */
    fun load(newTags: List<Tag>) {
        this.tags.clear()
        this.tags.addAll(newTags)
        notifyDataSetChanged()
    }

    /**
     * Add single tag into list
     */
    fun addTag(tag: Tag) {
        tags.add(0, tag)
        notifyItemInserted(0)
    }
}