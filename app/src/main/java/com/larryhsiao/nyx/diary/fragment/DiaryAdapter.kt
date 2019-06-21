package com.larryhsiao.nyx.diary.fragment

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.larryhsiao.nyx.diary.Diary
import com.silverhetch.aura.view.ViewHolder
import com.larryhsiao.nyx.R
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

/**
 * Recycler view adapter for for diary.
 */
class DiaryAdapter : RecyclerView.Adapter<ViewHolder>() {
    private val diaries = ArrayList<Diary>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_diary, parent, false
            )
        )
    }

    override fun getItemCount(): Int {
        return diaries.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        @SuppressLint("SetTextI18n")
        holder.rootView.findViewById<TextView>(R.id.itemDiary_title).text = """
            ${diaries[position].title()}
            ${SimpleDateFormat.getDateInstance().format(Calendar.getInstance().also { it.timeInMillis = diaries[position].timestamp()}.timeInMillis)}
        """.trimIndent()
    }

    /**
     * Update the entire list data
     */
    fun load(new: List<Diary>) {
        diaries.clear()
        diaries.addAll(new)
        notifyDataSetChanged()
    }

    /**
     * Add single [Diary] into list.
     */
    fun newDiary(new: Diary) {
        diaries.add(new)
        notifyItemInserted(diaries.size - 1)
    }
}