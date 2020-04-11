package com.larryhsiao.nyx.jot;

import android.location.Location;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;
import com.bumptech.glide.Glide;
import com.larryhsiao.nyx.LocationString;
import com.larryhsiao.nyx.R;
import com.larryhsiao.nyx.core.attachments.Attachment;
import com.larryhsiao.nyx.core.attachments.AttachmentsByJotId;
import com.larryhsiao.nyx.core.attachments.QueriedAttachments;
import com.larryhsiao.nyx.core.jots.Jot;
import com.silverhetch.aura.location.LocationAddress;
import com.silverhetch.aura.uri.UriMimeType;
import com.silverhetch.aura.view.ViewHolder;
import com.silverhetch.clotho.Source;

import java.sql.Connection;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import static androidx.swiperefreshlayout.widget.CircularProgressDrawable.LARGE;

/**
 * Adapter for showing Jot list
 */
public class JotListAdapter extends RecyclerView.Adapter<ViewHolder> {
    private final Source<Connection> db;
    private final List<Jot> data = new ArrayList<>();
    private final Function<Jot, Void> clicked;

    public JotListAdapter(Source<Connection> db, Function<Jot, Void> clicked) {
        this.db = db;
        this.clicked = clicked;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(
            R.layout.item_jot, parent, false
        ));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Location location = new Location("Constant");
        final Jot jot = data.get(position);

        location.setLongitude(jot.location()[0]);
        location.setLatitude(jot.location()[1]);
        String address = new LocationString(
            new LocationAddress(holder.itemView.getContext(), location).value()
        ).value();
        if (address != null) {
            address += "\n";
        } else {
            address = "";
        }
        holder.getTextView(R.id.itemJot_content).setText(
            jot.mood() + " " + jot.content() + "\n" +
                address +
                DateFormat.getDateInstance().format(new Date(jot.createdTime()))
        );
        List<Attachment> attachments = new QueriedAttachments(
            new AttachmentsByJotId(db, jot.id())
        ).value().stream().filter(attachment -> new UriMimeType(
                holder.itemView.getContext(),
                attachment.uri()
            ).value().startsWith("image")
        ).limit(4).collect(Collectors.toList());
        final ImageView image = holder.itemView.findViewById(R.id.itemJot_image);
        if (attachments.size() > 0) {
            image.setVisibility(View.VISIBLE);
            CircularProgressDrawable progress = new CircularProgressDrawable(image.getContext());
            progress.setStyle(LARGE);
            Glide.with(image.getContext())
                .load(attachments.get(0).uri())
                .placeholder(progress)
                .into(image);
        } else {
            image.setVisibility(View.GONE);
        }
        holder.getRootView().setOnClickListener(v -> clicked.apply(
            data.get(holder.getAdapterPosition()))
        );
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    /**
     * Load data into list
     */
    public void loadJots(List<Jot> data) {
        this.data.clear();
        this.data.addAll(data);
        notifyDataSetChanged();
    }

    /**
     * Append a Jot to list
     */
    public void insertJot(Jot jot) {
        boolean emptyFirst = data.size() == 0;
        data.add(0, jot);
        if (emptyFirst) {
            notifyDataSetChanged();
        } else {
            notifyItemInserted(data.size() - 1);
        }
    }

    /**
     * Update a Jot in list
     */
    public void updateJot(Jot updated) {
        for (int i = 0; i < data.size(); i++) {
            if (data.get(i).id() == updated.id()) {
                data.add(i, updated);
                data.remove(i + 1);
                break;
            }
        }
    }
}
