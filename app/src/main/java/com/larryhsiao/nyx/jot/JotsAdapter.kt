package com.larryhsiao.nyx.jot

import android.content.Context
import android.text.SpannableString
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.larryhsiao.nyx.R
import com.larryhsiao.nyx.core.jots.Jot
import com.larryhsiao.nyx.core.tags.QueriedTags
import com.larryhsiao.nyx.core.tags.TagsByJotId
import com.larryhsiao.nyx.utils.HashTagEnlightenAction
import com.silverhetch.clotho.Source
import kotlinx.android.synthetic.main.item_jot.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.sql.Connection
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

/**
 * Adapter to show jots.
 */
class JotsAdapter(
    private val db: Source<Connection>,
    private val lifeCoroutineScope: CoroutineScope,
    private val itemClicked: (item: Jot) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val dateFormat by lazy { SimpleDateFormat("MM/dd", Locale.US) }
    private val timeFormat by lazy { SimpleDateFormat("HH:mm", Locale.US) }
    private val jots = ArrayList<Jot>()
    private var showDate = false
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
        val jot = jots[position]
        holder.itemView.itemJot_textView.text = buildTitle(holder.itemView.context, jot)
        holder.itemView.itemJot_time_textView.text = if (showDate) {
            dateFormat.format(jotCalendar(jot).time) + " "
        } else {
            ""
        } + timeFormat.format(Date(jot.createdTime()))
        holder.itemView.setOnClickListener { itemClicked(jots[position]) }
        lifeCoroutineScope.launch {
            val tags = withContext(IO) {
                QueriedTags(TagsByJotId(db, jot.id())).value()
            }.map { it.title() to it }.toMap()
            if (holder.adapterPosition == position) {
                HashTagEnlightenAction(
                    holder.itemView.itemJot_textView,
                    buildTitle(holder.itemView.context, jot),
                    tags
                ).fire()
            }
        }
    }

    private fun buildTitle(context: Context, jot: Jot): String {
        if (jot.title().isNotEmpty()) {
            return jot.title()
        }
        if (jot.content().isNotEmpty()) {
            return jot.content()
        }
        return context.getString(R.string.No_title)
    }

    override fun getItemCount(): Int {
        return jots.size
    }

    fun load(newJots: List<Jot>) {
        jots.clear()
        jots.addAll(newJots)
        jots.sortBy { it.createdTime() }
        showDate = jots.size > 0 && jotCalendar(jots.first()) != jotCalendar(jots.last())
        notifyDataSetChanged()
    }

    private fun jotCalendar(jot: Jot): Calendar {
        return Calendar.getInstance().apply {
            timeInMillis = jots.first().createdTime()
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
    }
}