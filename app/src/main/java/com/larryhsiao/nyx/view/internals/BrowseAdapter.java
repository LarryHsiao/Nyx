package com.larryhsiao.nyx.view.internals;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.larryhsiao.nyx.R;
import com.silverhetch.aura.view.ViewHolder;
import com.silverhetch.aura.view.measures.DP;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * Adapter for browsing files.
 */
public class BrowseAdapter extends RecyclerView.Adapter<ViewHolder> {
    private final List<File> files = new ArrayList<>();
    private final Function<File, Boolean> onClicked;

    public BrowseAdapter(Function<File, Boolean> onClicked) {
        this.onClicked = onClicked;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.item_browse,
                        parent,
                        false
                )
        );
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        final File file = files.get(position);
        final TextView title = holder.getTextView(R.id.itemBrowse_title);
        title.setText(file.getName());
        title.setCompoundDrawablePadding(
                ((int) new DP(title.getContext(), 8).px())
        );
        if (file.isDirectory()) {
            if (file.getName().equals("..")) {
                title.setCompoundDrawablesRelativeWithIntrinsicBounds(
                        title.getResources().getDrawable(R.drawable.ic_back),
                        null,
                        null,
                        null);
            } else {
                title.setCompoundDrawablesRelativeWithIntrinsicBounds(
                        title.getResources().getDrawable(R.drawable.ic_folder),
                        null,
                        null,
                        null);
            }
        } else {
            title.setCompoundDrawablesRelativeWithIntrinsicBounds(
                    title.getResources().getDrawable(R.drawable.ic_file),
                    null,
                    null,
                    null);
        }
        holder.itemView.setOnClickListener(v -> onClicked.apply(file));
    }

    @Override
    public int getItemCount() {
        return files.size();
    }

    public void updateFiles(List<File> newFiles) {
        files.clear();
        files.addAll(newFiles);
        notifyDataSetChanged();
    }
}
