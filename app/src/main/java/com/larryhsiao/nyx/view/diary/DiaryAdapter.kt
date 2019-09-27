package com.larryhsiao.nyx.view.diary

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.larryhsiao.nyx.R
import com.larryhsiao.nyx.diary.Diary
import com.silverhetch.aura.view.ViewHolder
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_diary.view.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

/**
 * Recycler view adapter for for diary.
 */
class DiaryAdapter(private val onItemClicked: (item: Diary) -> Unit) :
    RecyclerView.Adapter<ViewHolder>() {
    private val diaries = ArrayList<Diary>()
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
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
        holder.rootView.itemDiary_title.text = """
            ${diaries[position].title()}
            ${SimpleDateFormat.getDateInstance().format(Calendar.getInstance().also {
            it.timeInMillis = diaries[position].timestamp()
        }.timeInMillis)}
        """.trimIndent()
        holder.rootView.itemDiary_image.also {
            val images = diaries[position].imageUris()
                .filter { imageUri ->
                    imageUri.toString().startsWith("file:")
                }
            if (images.isEmpty()) {
                it.visibility = View.GONE
            } else {
                it.visibility = View.VISIBLE
                Picasso.get().load(images[0])
                    .placeholder(CircularProgressDrawable(it.context).apply {
                        setStyle(CircularProgressDrawable.LARGE)
                    }).into(it)
            }
        }

        holder.rootView.setOnClickListener {
            onItemClicked(diaries[holder.adapterPosition])
        }
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