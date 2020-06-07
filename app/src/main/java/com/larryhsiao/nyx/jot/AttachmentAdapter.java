package com.larryhsiao.nyx.jot;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.larryhsiao.nyx.R;
import com.larryhsiao.nyx.attachments.IsLocalExist;
import com.larryhsiao.nyx.attachments.JotImageLoading;
import com.silverhetch.aura.uri.UriMimeType;
import com.silverhetch.aura.view.recyclerview.ViewHolder;
import com.stfalcon.imageviewer.StfalconImageViewer;
import io.github.ponnamkarthik.richlinkpreview.MetaData;
import io.github.ponnamkarthik.richlinkpreview.ResponseListener;
import io.github.ponnamkarthik.richlinkpreview.RichPreview;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static android.content.Intent.ACTION_VIEW;
import static android.graphics.Color.WHITE;

/**
 * Adapter for displaying attachments.
 */
public class AttachmentAdapter extends RecyclerView.Adapter<ViewHolder> {
    private static final int ITEM_TYPE_IMAGE = 1;
    private static final int ITEM_TYPE_VIDEO = 2;
    private static final int ITEM_TYPE_AUDIO = 3;
    private static final int ITEM_TYPE_PREVIEW_URL = 4;

    private final Context context;
    private final List<Uri> data = new ArrayList<>();

    public AttachmentAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case ITEM_TYPE_AUDIO:
                return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.item_attachment_audio,
                    parent,
                    false
                ));
            case ITEM_TYPE_VIDEO:
                return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.item_attachment_video,
                    parent,
                    false
                ));
            case ITEM_TYPE_PREVIEW_URL:
                return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.item_attachment_link_preview,
                    parent,
                    false
                ));
            default:
            case ITEM_TYPE_IMAGE:
                return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.item_attachment_image,
                    parent,
                    false
                ));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        final Uri uri = data.get(position);
        switch (getItemViewType(position)) {
            default:
            case ITEM_TYPE_IMAGE:
                onBindImage(uri, holder, () -> showFullScreenImage(context, uri));
                break;
            case ITEM_TYPE_VIDEO:
                onBindVideo(uri, holder);
                break;
            case ITEM_TYPE_AUDIO:
                onBindAudio(uri, holder);
                break;
            case ITEM_TYPE_PREVIEW_URL:
                onBindPreview(uri, holder);
                break;
        }
    }

    private void onBindPreview(Uri uri, ViewHolder holder) {
        RichPreview preview = new RichPreview(new ResponseListener() {
            @Override
            public void onData(MetaData metaData) {
                final View root = holder.getRootView();
                ImageView icon = root.findViewById(R.id.itemUrlPreview_icon);
                Glide.with(icon.getContext())
                    .load(metaData.getImageurl())
                    .into(icon);
                TextView urlText = root.findViewById(R.id.itemUrlPreview_urlText);
                urlText.setText(metaData.getUrl());
                TextView title = root.findViewById(R.id.itemUrlPreview_title);
                title.setText(metaData.getTitle());
                root.setOnClickListener(it ->
                    it.getContext().startActivity(new Intent(ACTION_VIEW, uri))
                );
                root.setOnLongClickListener(it -> {
                    showProperties(holder, uri);
                    return true;
                });
            }

            @Override
            public void onError(Exception e) {

            }
        });
        preview.getPreview(uri.toString());
    }

    private void showFullScreenImage(Context context, Uri uri) {
        new StfalconImageViewer.Builder<>(
            context,
            Collections.singletonList(uri),
            (imageView, image) -> {
                new JotImageLoading(imageView, uri.toString(), WHITE).fire();
            }
        ).show();
    }

    private void onBindAudio(Uri uri, ViewHolder holder) {
        ImageView imageView = holder.itemView.findViewById(R.id.itemAttachmentAudio_icon);
        if (new IsLocalExist(context, uri.toString()).value()) {
            imageView.setOnClickListener(v -> {
                final Intent intent = new Intent(ACTION_VIEW);
                intent.setDataAndType(uri, "audio/*");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                context.startActivity(intent);
            });
            imageView.setOnLongClickListener(v -> {
                showProperties(holder, uri);
                return true;
            });
        } else {
            imageView.setImageResource(R.drawable.ic_syncing);
            imageView.setImageTintList(ColorStateList.valueOf(WHITE));
            imageView.setOnClickListener(v ->
                Toast.makeText(
                    imageView.getContext(),
                    R.string.File_no_yet_synced,
                    Toast.LENGTH_SHORT
                ).show()
            );
        }
    }

    private void onBindVideo(Uri uri, ViewHolder holder) {
        ImageView imageView = holder.itemView.findViewById(R.id.itemAttachmentVideo_icon);
        if (new IsLocalExist(context, uri.toString()).value()) {
            MediaMetadataRetriever mmr = new MediaMetadataRetriever();
            mmr.setDataSource(context, uri);
            imageView.setImageBitmap(mmr.getFrameAtTime());
            imageView.setOnClickListener(v -> {
                final Intent intent = new Intent(ACTION_VIEW);
                intent.setDataAndType(uri, "video/*");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                context.startActivity(intent);
            });
            mmr.release();
        } else {
            imageView.setImageResource(R.drawable.ic_syncing);
            imageView.setImageTintList(ColorStateList.valueOf(WHITE));
            imageView.setOnClickListener(v ->
                Toast.makeText(
                    imageView.getContext(),
                    R.string.File_no_yet_synced,
                    Toast.LENGTH_SHORT
                ).show()
            );
        }
        imageView.setOnLongClickListener(v -> {
            showProperties(holder, uri);
            return true;
        });
    }

    private void onBindImage(Uri uri, ViewHolder holder, Runnable onClick) {
        final ImageView icon = holder.getImageView(R.id.itemAttachmentImage_icon);
        new JotImageLoading(icon, uri.toString(), WHITE).fire();
        icon.setOnClickListener(v -> {
            onClick.run();
        });
        icon.setOnLongClickListener(v -> {
            showProperties(holder, uri);
            return true;
        });
    }

    private void showProperties(ViewHolder holder, Uri uri) {
        final PopupMenu popup = new PopupMenu(context, holder.itemView);
        popup.getMenu().add(R.string.delete).setOnMenuItemClickListener(item -> {
            int index = holder.getAdapterPosition();
            data.remove(index);
            notifyItemRemoved(index);
            return true;
        });
        popup.getMenu()
            .add(context.getString(R.string.properties))
            .setOnMenuItemClickListener(item -> {
                final AlertDialog dialog = new AlertDialog.Builder(context)
                    .setView(R.layout.dialog_properties)
                    .show();
                ((TextView) dialog.findViewById(R.id.properties_text)).setText(
                    "Uri: " + uri.toString()
                );
                return true;
            });
        popup.show();
    }

    @Override
    public int getItemViewType(int position) {
        final Uri uri = data.get(position);
        final String mimeType = new UriMimeType(
            context,
            uri.toString()
        ).value();
        if (mimeType.startsWith("video")) {
            return ITEM_TYPE_VIDEO;
        } else if (mimeType.startsWith("audio")) {
            return ITEM_TYPE_AUDIO;
        } else if (mimeType.startsWith("image")) {
            return ITEM_TYPE_IMAGE;
        } else {
            if (uri.toString().startsWith("http")) {
                return ITEM_TYPE_PREVIEW_URL;
            }
            return ITEM_TYPE_IMAGE;
        }
    }

    /**
     * Determine if the given position should have full span.
     */
    public boolean isFullSpan(int position) {
        return getItemViewType(position) == ITEM_TYPE_PREVIEW_URL;
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    /**
     * Load attachments
     */
    public void loadAttachments(List<Uri> data) {
        this.data.clear();
        this.data.addAll(data);
        notifyDataSetChanged();
    }

    /**
     * Append an attachment to list
     */
    public void append(Uri uri) {
        this.data.add(uri);
        notifyItemInserted(data.size() - 1);
    }

    /**
     * Export uris
     */
    public List<Uri> exportUri() {
        return data;
    }
}
