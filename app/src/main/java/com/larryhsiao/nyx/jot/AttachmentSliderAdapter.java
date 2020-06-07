package com.larryhsiao.nyx.jot;

import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.larryhsiao.nyx.R;
import com.larryhsiao.nyx.attachments.IsLocalExist;
import com.larryhsiao.nyx.attachments.JotImageLoading;
import com.silverhetch.aura.uri.UriMimeType;
import com.silverhetch.aura.view.recyclerview.ViewHolder;
import io.github.ponnamkarthik.richlinkpreview.MetaData;
import io.github.ponnamkarthik.richlinkpreview.ResponseListener;
import io.github.ponnamkarthik.richlinkpreview.RichPreview;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter for showing slide view of attachment.
 */
public class AttachmentSliderAdapter extends RecyclerView.Adapter<ViewHolder> {
    public interface OnClickListener {
        void onClicked(View view, String uri, boolean longClicked);
    }

    private static final int TYPE_IMAGE = 1;
    private static final int TYPE_VIDEO = 2;
    private static final int TYPE_AUDIO = 3;
    private static final int TYPE_HTTP_LINK = 4;

    private final Context context;
    private final List<String> sliderUris = new ArrayList<>();
    private final OnClickListener clicked;

    public AttachmentSliderAdapter(
        Context context,
        OnClickListener clicked
    ) {
        this.context = context;
        this.clicked = clicked;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int type) {
        switch (type) {
            case TYPE_HTTP_LINK:
                return new ViewHolder(
                    LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.item_attachment_link_preview, parent, false
                    )
                );
            case TYPE_AUDIO:
                return new ViewHolder(
                    LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.item_attachment_audio, parent, false
                    )
                );
            case TYPE_VIDEO:
                return new ViewHolder(
                    LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.item_attachment_video, parent, false
                    )
                );
            case TYPE_IMAGE:
            default:
                return new ViewHolder(
                    LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.item_attachment_image, parent, false)
                );
        }
    }

    @Override
    public int getItemViewType(int position) {
        final String uri = sliderUris.get(position);
        if (uri.startsWith("http")) {
            return TYPE_HTTP_LINK;
        }
        final String mimeType = new UriMimeType(context, uri).value();
        if (mimeType.startsWith("image")) {
            return TYPE_IMAGE;
        } else if (mimeType.startsWith("audio")) {
            return TYPE_AUDIO;
        } else if (mimeType.startsWith("video")) {
            return TYPE_VIDEO;
        } else {
            return TYPE_IMAGE;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case TYPE_HTTP_LINK:
                onBindLinkPreview(
                    holder,
                    Uri.parse(sliderUris.get(position))
                );
                break;
            case TYPE_AUDIO:
                onBindAudio(holder.itemView, sliderUris.get(position));
                break;
            case TYPE_VIDEO:
                onBindVideo(holder.itemView, sliderUris.get(position));
                break;
            case TYPE_IMAGE:
            default:
                onBindImage(holder.itemView, sliderUris.get(position));
                break;
        }
        holder.itemView.setOnClickListener(v ->
            clicked.onClicked(v, sliderUris.get(position), false)
        );

        holder.itemView.setOnLongClickListener(v -> {
            clicked.onClicked(v, sliderUris.get(position), true);
            return true;
        });
    }

    private void onBindVideo(View view, String uri) {
        boolean isLocalExist = new IsLocalExist(view.getContext(), uri).value();
        ImageView imageView = view.findViewById(R.id.itemAttachmentVideo_icon);
        if (isLocalExist) {
            MediaMetadataRetriever mmr = new MediaMetadataRetriever();
            mmr.setDataSource(view.getContext(), Uri.parse(uri));
            imageView.setImageBitmap(mmr.getFrameAtTime());
            mmr.release();
        } else {
            imageView.setImageResource(R.drawable.ic_syncing);
        }
    }

    private void onBindAudio(View root, String uri) {
        boolean isLocalExist = new IsLocalExist(root.getContext(), uri).value();
        ImageView imageView = root.findViewById(R.id.itemAttachmentAudio_icon);
        if (!isLocalExist) {
            imageView.setImageResource(R.drawable.ic_syncing);
        }
    }

    private void onBindImage(View view, String uri) {
        final ImageView icon = view.findViewById(R.id.itemAttachmentImage_icon);
        new JotImageLoading(icon, uri).fire();
    }

    private void onBindLinkPreview(ViewHolder holder, Uri attachmentUri) {
        final View root = holder.itemView;
        final ImageView icon = root.findViewById(R.id.itemUrlPreview_icon);
        final TextView content = root.findViewById(R.id.itemUrlPreview_title);
        final TextView urlText = root.findViewById(R.id.itemUrlPreview_urlText);

        RichPreview preview = new RichPreview(new ResponseListener() {
            @Override
            public void onData(MetaData metaData) {
                Glide.with(root.getContext())
                    .load(metaData.getImageurl())
                    .into(icon);
                content.setText(metaData.getTitle());
                urlText.setText(metaData.getUrl());
            }

            @Override
            public void onError(Exception e) {
                icon.setImageResource(R.drawable.ic_warning);
            }
        });
        preview.getPreview(attachmentUri.toString());
    }

    @Override
    public int getItemCount() {
        return sliderUris.size();
    }

    public void removeAll() {
        sliderUris.clear();
        notifyDataSetChanged();
    }

    public void renewItems(List<String> sliderItems) {
        sliderUris.clear();
        this.sliderUris.addAll(sliderItems);
        notifyDataSetChanged();
    }
}