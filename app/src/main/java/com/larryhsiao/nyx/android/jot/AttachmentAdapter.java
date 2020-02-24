package com.larryhsiao.nyx.android.jot;

import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;
import com.larryhsiao.nyx.R;
import com.silverhetch.aura.view.ViewHolder;
import com.squareup.picasso.Picasso;
import com.stfalcon.imageviewer.StfalconImageViewer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
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
            final ImageView attchmentIcon = holder.getImageView(R.id.itemAttachment_icon);
            final Uri uri = data.get(position);
            final String mimeType = contentResolver.getType(uri);
            if ((mimeType != null && mimeType.startsWith("image/")) || uri.toString().startsWith("file")) {
                final Bitmap iconBitmap;
                if (uri.toString().startsWith("file")) {
                    iconBitmap = BitmapFactory.decodeFile(uri.toString().replace("file:",""));
                } else {
                    if (SDK_INT >= P) {
                        iconBitmap = ImageDecoder.decodeBitmap(ImageDecoder.createSource(contentResolver, uri));
                    } else {
                        iconBitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri);
                    }
                }
                attchmentIcon.setImageBitmap(iconBitmap);
                attchmentIcon.setOnClickListener(v -> {
                    new StfalconImageViewer.Builder<>(
                        attchmentIcon.getContext(),
                        Collections.singletonList(uri),
                        (imageView, image) -> Picasso.get().load(image).into(imageView)).show();
                });
                attchmentIcon.setOnLongClickListener(v -> {
                    final PopupMenu popup = new PopupMenu(attchmentIcon.getContext(), holder.itemView);
                    popup.getMenu().add(R.string.delete).setOnMenuItemClickListener(item -> {
                        int index = holder.getAdapterPosition();
                        data.remove(index);
                        notifyItemRemoved(index);
                        return true;
                    });
                    popup.getMenu()
                        .add(attchmentIcon.getContext().getString(R.string.properties))
                        .setOnMenuItemClickListener(item -> {
                            final AlertDialog dialog = new AlertDialog.Builder(attchmentIcon.getContext())
                                .setView(R.layout.dialog_properties)
                                .show();
                            ((TextView) dialog.findViewById(R.id.properties_text)).setText(
                                "Uri: " + uri.toString()
                            );
                            return true;
                        });
                    popup.show();
                    return true;
                });
            } else {
                attchmentIcon.setImageDrawable(null); // clear image
                attchmentIcon.setOnClickListener(null);
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
