package com.larryhsiao.nyx.utils

import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.larryhsiao.nyx.core.attachments.Attachment
import com.larryhsiao.aura.view.measures.DP
import com.squareup.picasso.Picasso
import jp.wasabeef.picasso.transformations.BlurTransformation

/**
 * Adapter for pager which has one image
 */
class AttachmentPagerAdapter(
        private val uris: ArrayList<Attachment>,
        private val blurImage: Boolean,
        private val itemClicked: (attachment: Attachment) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
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
            Picasso.get()
                .load(uris[position].uri())
                .apply {
                    if (blurImage){
                        transform(BlurTransformation(context, 25, 3))
                    }
                }
                .placeholder(placeHolder)
                .into(this)
        }
    }

    override fun getItemCount(): Int {
        return uris.size
    }
}