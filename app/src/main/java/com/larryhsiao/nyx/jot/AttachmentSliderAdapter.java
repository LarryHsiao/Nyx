package com.larryhsiao.nyx.jot;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.larryhsiao.nyx.R;
import com.larryhsiao.nyx.attachments.JotImageLoading;
import com.larryhsiao.nyx.core.attachments.Attachment;
import com.smarteist.autoimageslider.SliderViewAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import static com.larryhsiao.nyx.jot.AttachmentSliderAdapter.SliderVH;

/**
 * Adapter for showing slide view of attachment.
 */
public class AttachmentSliderAdapter extends SliderViewAdapter<SliderVH> {
    private final List<Attachment> mSliderItems = new ArrayList<>();
    private final Function<Attachment, Void> clicked;

    public AttachmentSliderAdapter(Function<Attachment, Void> clicked) {
        this.clicked = clicked;
    }

    @Override
    public SliderVH onCreateViewHolder(ViewGroup parent) {
        return new SliderVH(
            ((ImageView) LayoutInflater.from(parent.getContext()).inflate(
                R.layout.component_image, parent, false
            )));
    }

    @Override
    public void onBindViewHolder(SliderVH vh, final int position) {
        Attachment sliderItem = mSliderItems.get(position);
        new JotImageLoading(vh.itemView, sliderItem.uri()).fire();
        vh.itemView.setOnClickListener(v -> clicked.apply(sliderItem));
    }

    @Override
    public int getCount() {
        return mSliderItems.size();
    }

    public void removeAll() {
        mSliderItems.clear();
        notifyDataSetChanged();
    }

    public void renewItems(List<Attachment> sliderItems) {
        mSliderItems.clear();
        this.mSliderItems.addAll(sliderItems);
        notifyDataSetChanged();
    }

    public static class SliderVH extends SliderViewAdapter.ViewHolder {
        ImageView itemView;

        public SliderVH(ImageView itemView) {
            super(itemView);
            this.itemView = itemView;
        }
    }
}