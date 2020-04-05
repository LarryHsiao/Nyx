package com.larryhsiao.nyx.jot;

import android.content.Context;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;
import com.larryhsiao.nyx.R;
import com.silverhetch.aura.uri.UriMimeType;
import com.silverhetch.aura.view.ViewHolder;
import com.squareup.picasso.Picasso;
import com.stfalcon.imageviewer.StfalconImageViewer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static androidx.swiperefreshlayout.widget.CircularProgressDrawable.LARGE;

/**
 * Adapter for displaying attachments.
 */
public class AttachmentAdapter extends RecyclerView.Adapter<ViewHolder> {
    private static final int ITEM_TYPE_IMAGE = 1;
    private static final int ITEM_TYPE_VIDEO = 2;

    private final Context context;
    private final List<Uri> data = new ArrayList<>();

    public AttachmentAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case ITEM_TYPE_VIDEO:
                return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.item_attachment_video,
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
                onBindImage(uri, holder);
                break;
            case ITEM_TYPE_VIDEO:
                onBindVideo(uri, holder);
                break;
        }
    }

    private void onBindVideo(Uri uri, ViewHolder holder) {
        try (MediaMetadataRetriever mmr = new MediaMetadataRetriever()) {
            mmr.setDataSource(context, uri);
            ImageView imageView = holder.itemView.findViewById(R.id.itemAttachmentVideo_icon);
            imageView.setImageBitmap(mmr.getFrameAtTime());
            imageView.setOnClickListener(v -> {
                final Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(uri, "video/*");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                context.startActivity(intent);
            });
            imageView.setOnLongClickListener(v -> {
                showProperties(holder, uri);
                return true;
            });
        }
    }

    private void onBindImage(Uri uri, ViewHolder holder) {
        final ImageView attachmentIcon = holder.getImageView(R.id.itemAttachmentImage_icon);
        CircularProgressDrawable progressDrawable = new CircularProgressDrawable(attachmentIcon.getContext());
        progressDrawable.setStyle(LARGE);
        Picasso.get().load(uri).placeholder(progressDrawable).into(attachmentIcon);
        attachmentIcon.setOnClickListener(v -> {
            new StfalconImageViewer.Builder<>(
                attachmentIcon.getContext(),
                Collections.singletonList(uri),
                (imageView, image) -> {
                    CircularProgressDrawable progressDrawable2 = new CircularProgressDrawable(attachmentIcon.getContext());
                    progressDrawable2.setStyle(LARGE);
                    Picasso.get()
                        .load(image)
                        .placeholder(progressDrawable2)
                        .into(imageView);
                }
            ).show();
        });
        attachmentIcon.setOnLongClickListener(v -> {
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
        } else {
            return ITEM_TYPE_IMAGE;
        }
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
