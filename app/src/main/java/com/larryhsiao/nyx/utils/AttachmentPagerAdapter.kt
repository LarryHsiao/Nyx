package com.larryhsiao.nyx.utils

import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.larryhsiao.aura.view.measures.DP
import com.larryhsiao.nyx.core.attachments.Attachment
import com.larryhsiao.nyx.core.attachments.Attachments
import jp.wasabeef.glide.transformations.BlurTransformation

/**
 * Adapter for pager which has one image
 */
class AttachmentPagerAdapter(
        private val blurImage: Boolean,
        private val itemClicked: (attachmentUri: String) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val uris: ArrayList<String> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return object : RecyclerView.ViewHolder(
            ImageView(parent.context).apply {
                val size = DP(parent.context, 150f).px()
                layoutParams = ViewGroup.LayoutParams(size.toInt(), size.toInt())
                scaleType = ImageView.ScaleType.CENTER_CROP
            }
        ) {}
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder.itemView as ImageView).apply {
            setOnClickListener { itemClicked(uris[position]) }
            val placeHolder = CircularProgressDrawable(context)
            placeHolder.setStyle(CircularProgressDrawable.DEFAULT)
            Glide.with(holder.itemView)
                .load(uris[position])
                .apply{
                    if (blurImage){
                        transform(BlurTransformation(25, 4))
                    }
                }
                .placeholder(CircularProgressDrawable(holder.itemView.context))
                .into(this)
        }
    }

    fun loadUp(newAttachments: List<String>) {
        this.uris.clear()
        this.uris.addAll(newAttachments)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return uris.size
    }
}