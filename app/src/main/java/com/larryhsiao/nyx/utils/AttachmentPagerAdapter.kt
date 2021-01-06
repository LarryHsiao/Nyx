package com.larryhsiao.nyx.utils

import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.ImageView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import androidx.viewpager.widget.PagerAdapter
import com.larryhsiao.nyx.core.attachments.Attachment
import com.squareup.picasso.Picasso

/**
 * Adapter for pager which has one image
 */
class AttachmentPagerAdapter(
        private val uris: ArrayList<Attachment>,
        private val itemClicked: (attachment: Attachment) -> Unit
) : PagerAdapter() {
    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun getCount(): Int {
        return uris.size
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val image = ImageView(container.context)
        image.layoutParams = ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT)
        image.scaleType = ImageView.ScaleType.CENTER_CROP
        image.setOnClickListener { itemClicked(uris[position]) }
        val placeHolder = CircularProgressDrawable(container.context)
        placeHolder.setStyle(CircularProgressDrawable.DEFAULT)
        Picasso.get().load(uris[position].uri()).placeholder(placeHolder).into(image)
        container.addView(image)
        return image
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View?)
    }
}