package com.larryhsiao.nyx.tag;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.larryhsiao.nyx.core.tags.Tag;
import com.silverhetch.aura.view.recyclerview.ViewHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * Adapter for displaying tag list
 */
public class TagListAdapter extends RecyclerView.Adapter<ViewHolder> {
    private final List<Tag> data = new ArrayList<>();
    private final Function<Tag, Object> clicked;
    private final Function<Tag, Object> longClicked;

    public TagListAdapter(
        Function<Tag, Object> clicked,
        Function<Tag, Object> longClicked
    ) {
        this.clicked = clicked;
        this.longClicked = longClicked;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(
            android.R.layout.simple_list_item_1,
            parent,
            false
        ));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ((TextView) holder.getRootView()).setText(data.get(position).title());
        holder.itemView.setOnClickListener(v ->
            clicked.apply(data.get(holder.getAdapterPosition()))
        );
        holder.itemView.setOnLongClickListener(v -> {
            longClicked.apply(data.get(holder.getAdapterPosition()));
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public List<Tag> tags() {
        return data;
    }

    /**
     * Load entire list.
     */
    public void loadTags(List<Tag> newData) {
        data.clear();
        data.addAll(newData);
        notifyDataSetChanged();
    }

    /**
     * Append New Tag to list.
     */
    public void appendTag(Tag newTag) {
        boolean emptyFirst = data.size() == 0;
        data.add(newTag);
        if (emptyFirst) {
            notifyDataSetChanged();
        } else {
            notifyItemInserted(data.size() - 1);
        }
    }

    /**
     * Remove Tag from list.
     */
    public void removeTag(Tag tag) {
        int idx = data.indexOf(tag);
        data.remove(idx);
        notifyItemRemoved(idx);
    }
}
