package com.larryhsiao.nyx.android.jot;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.larryhsiao.nyx.R;
import com.silverhetch.aura.view.ViewHolder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static android.os.Build.VERSION.SDK_INT;
import static android.os.Build.VERSION_CODES.P;

/**
 * Adapter for displaying attachments.
 */
public class AttachmentAdapter extends RecyclerView.Adapter<ViewHolder> {
    private final List<Uri> data = new ArrayList<>();

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(
            R.layout.item_attachment,
            parent,
            false
        ));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        try {
            final ContentResolver contentResolver = holder.itemView.getContext().getContentResolver();
            final ImageView imageView = holder.getImageView(R.id.itemAttachment_icon);
            final Uri uri = data.get(position);
            final String mimeType = contentResolver.getType(uri);
            if (mimeType != null && mimeType.startsWith("image/")) {
                final Bitmap iconBitmap;
                if (SDK_INT >= P) {
                    iconBitmap = ImageDecoder.decodeBitmap(ImageDecoder.createSource(contentResolver, uri));
                } else {
                    iconBitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri);
                }
                imageView.setImageBitmap(iconBitmap);
            } else {
                imageView.setImageDrawable(null); // clear image
            }
        } catch (IOException e) {
            e.printStackTrace();
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
     * Append a attachment to list
     */
    public void appendImage(Uri uri) {
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
