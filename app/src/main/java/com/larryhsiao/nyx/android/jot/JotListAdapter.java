package com.larryhsiao.nyx.android.jot;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.larryhsiao.nyx.R;
import com.larryhsiao.nyx.jots.Jot;
import com.silverhetch.aura.view.ViewHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * Adapter for showing Jot list
 */
public class JotListAdapter extends RecyclerView.Adapter<ViewHolder> {
    private final List<Jot> data = new ArrayList<>();
    private final Function<Jot, Void> clicked;

    public JotListAdapter(Function<Jot, Void> clicked) {
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
        holder.getTextView(R.id.itemJot_content).setText(data.get(position).content());
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
        data.add(0, jot);
        notifyItemInserted(data.size() - 1);
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
