package com.larryhsiao.nyx.old.jot;

import android.location.Location;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;
import com.larryhsiao.nyx.R;
import com.larryhsiao.nyx.core.attachments.Attachment;
import com.larryhsiao.nyx.core.attachments.AttachmentsByJotId;
import com.larryhsiao.nyx.core.attachments.QueriedAttachments;
import com.larryhsiao.nyx.core.jots.Jot;
import com.larryhsiao.nyx.old.attachments.AttachmentPropertiesDialog;
import com.larryhsiao.nyx.old.attachments.LaunchAttachment;
import com.larryhsiao.aura.view.measures.DP;
import com.larryhsiao.aura.view.recyclerview.ViewHolder;
import com.larryhsiao.aura.view.recyclerview.slider.DotIndicatorDecoration;
import com.larryhsiao.aura.view.recyclerview.slider.Slider;
import com.larryhsiao.clotho.Source;
import com.larryhsiao.clotho.date.DateCalendar;

import java.sql.Connection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static android.view.LayoutInflater.from;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static java.text.DateFormat.getDateInstance;
import static java.util.Calendar.getInstance;
import static java.util.stream.Collectors.toList;

/**
 * Adapter for showing Jot list
 */
public class JotListAdapter extends RecyclerView.Adapter<ViewHolder> {
    public interface OnClickListener {
        void onClicked(Jot uri);
    }

    private static final int ITEM_TOP = 1;
    private static final int ITEM_MIDDLE = 2;
    private static final int ITEM_END = 3;
    private final Source<Connection> db;
    private final List<Jot> data = new ArrayList<>();
    private final OnClickListener clicked;

    public JotListAdapter(Source<Connection> db, OnClickListener clicked) {
        this.db = db;
        this.clicked = clicked;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(
        @NonNull ViewGroup parent, int viewType) {
        ViewHolder holder = new ViewHolder(from(parent.getContext()).inflate(
            R.layout.item_jot_old, parent, false
        ));
        RecyclerView slider = holder.itemView.findViewById(R.id.itemJot_image);
        new Slider(slider).fire();
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Location location = new Location("Constant");
        final Jot jot = data.get(position);
        location.setLongitude(jot.location()[0]);
        location.setLatitude(jot.location()[1]);
        holder.getTextView(R.id.itemJot_title).setText(
            getDateInstance().format(new Date(jot.createdTime()))
        );
        holder.getTextView(R.id.itemJot_content).setText(
            (jot.mood() + " " + jot.content() + "\n").trim()
        );
        List<Attachment> attachments = new QueriedAttachments(
            new AttachmentsByJotId(db, jot.id())
        ).value();
        final RecyclerView image =
            holder.itemView.findViewById(R.id.itemJot_image);
        final AttachmentSliderAdapter adapter = new AttachmentSliderAdapter(
            image.getContext(),
            (view, uri, longClicked) -> {
                if (longClicked) {
                    showAttachmentProperties(view, uri);
                } else {
                    new LaunchAttachment(view.getContext(), uri).fire();
                }
            }
        );
        image.setAdapter(adapter);
        if (attachments.size() > 0) {
            adapter.renewItems(attachments.stream()
                .map(Attachment::uri)
                .collect(toList()));
            image.setVisibility(VISIBLE);
            for (int i = 0; i < image.getItemDecorationCount(); i++) {
                RecyclerView.ItemDecoration decoration = image.getItemDecorationAt(i);
                if (decoration instanceof DotIndicatorDecoration) {
                    ((DotIndicatorDecoration) decoration).attachTo(image);
                }
            }
        } else {
            image.setVisibility(GONE);
        }
        holder.itemView.setOnClickListener(v -> clicked.onClicked(jot));
        int margin = ((int) new DP(holder.itemView.getContext(), 4).px());
        RecyclerView.LayoutParams layoutParams =
            (RecyclerView.LayoutParams) holder.itemView.getLayoutParams();
        switch (getItemViewType(position)) {
            case ITEM_END:
                layoutParams.bottomMargin = margin;
                layoutParams.topMargin = 0;
                holder.getTextView(R.id.itemJot_title).setText(
                    SimpleDateFormat.getTimeInstance(DateFormat.SHORT)
                        .format(new Date(jot.createdTime()))
                );
                break;
            case ITEM_MIDDLE:
                layoutParams.topMargin = 0;
                layoutParams.bottomMargin = 0;
                holder.getTextView(R.id.itemJot_title).setText(
                    SimpleDateFormat.getTimeInstance(DateFormat.SHORT)
                        .format(new Date(jot.createdTime()))
                );
                break;
            case ITEM_TOP:
            default:
                layoutParams.topMargin = margin;
                layoutParams.bottomMargin = 0;
                if (position != data.size() - 1 &&
                    new DateCalendar(jot.createdTime(), Calendar.getInstance())
                        .value().equals(
                        new DateCalendar(data.get(position + 1).createdTime(),
                            Calendar.getInstance()).value()
                    )
                ) {
                    holder.getTextView(R.id.itemJot_title).append(
                        String.format(
                            "\n%s",
                            SimpleDateFormat.getTimeInstance(DateFormat.SHORT)
                                .format(new Date(jot.createdTime()))
                        )
                    );
                }
                break;
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return ITEM_TOP;
        }

        Calendar current = new DateCalendar(
            data.get(position).createdTime(),
            getInstance()
        ).value();
        Calendar previous = new DateCalendar(
            data.get(position - 1).createdTime(),
            getInstance()
        ).value();

        if (position == data.size() - 1) {
            if (!current.equals(previous)) {
                return ITEM_TOP;
            } else {
                return ITEM_END;
            }
        }

        Calendar next = new DateCalendar(
            data.get(position + 1).createdTime(),
            getInstance()
        ).value();
        if (current.equals(previous) && current.equals(next)) {
            return ITEM_MIDDLE;
        } else if (current.equals(previous) && !current.equals(next)) {
            return ITEM_END;
        } else {
            return ITEM_TOP;
        }
    }

    private void showAttachmentProperties(View view, String uri) {
        final PopupMenu popup = new PopupMenu(view.getContext(), view);
        popup.getMenu()
            .add(view.getContext().getString(R.string.properties))
            .setOnMenuItemClickListener(item -> {
                new AttachmentPropertiesDialog(view.getContext(), Uri.parse(uri)).fire();
                return true;
            });
        popup.show();
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    /**
     * Load data into list
     */
    public void loadJots(List<Jot> newData) {
        this.data.clear();
        data.addAll(newData.stream().sorted((o1, o2) -> (int) (
            o2.createdTime() / 1000f - o1.createdTime() / 1000f
        )).collect(toList()));
        notifyDataSetChanged();
    }

    private String dateStr(Jot jot) {
        return getDateInstance().format(new Date(jot.createdTime()));
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
