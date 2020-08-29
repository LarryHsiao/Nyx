package com.larryhsiao.nyx.jot

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.larryhsiao.nyx.R
import com.larryhsiao.nyx.core.jots.Jot
import kotlinx.android.synthetic.main.item_jot.view.*

/**
 * Adapter to show jots.
 */
class JotsAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val jots = ArrayList<Jot>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return object : RecyclerView.ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_jot,
                parent,
                false
            )
        ) {}
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder.itemView.itemJot_textView.text = jots[position].content()
    }

    override fun getItemCount(): Int {
        return jots.size
    }

    fun load(newJots:List<Jot>){
        jots.clear()
        jots.addAll(newJots)
        notifyDataSetChanged()
    }
}