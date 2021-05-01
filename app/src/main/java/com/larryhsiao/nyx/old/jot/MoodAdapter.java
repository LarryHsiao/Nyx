package com.larryhsiao.nyx.old.jot;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import com.larryhsiao.nyx.R;
import com.larryhsiao.aura.view.measures.DP;

import java.util.List;

import static android.util.TypedValue.COMPLEX_UNIT_DIP;
import static android.view.Gravity.CENTER;
import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

public class MoodAdapter extends ArrayAdapter<String> {

    public  MoodAdapter(@NonNull Context context, List<String> moods) {
        super(context,
            android.R.layout.simple_list_item_1,
            moods
        );
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convert, @NonNull ViewGroup parent) {
        if (position == getCount() - 1) { // last item for input dialog
            return inputItem(parent);
        }
        if (position == 0) { // first item to remove mood
            return removalItem(parent);
        }
        return orgItemView(position, parent);
    }

    private View orgItemView(int position, ViewGroup parent) {
        final AppCompatTextView orgItemView = ((AppCompatTextView) super.getView(
            position,
            null,
            parent)
        );
        orgItemView.setGravity(CENTER);
        orgItemView.setLayoutParams(new ViewGroup.LayoutParams(
            MATCH_PARENT,
            parent.getWidth() / 4));
        orgItemView.setTextSize(COMPLEX_UNIT_DIP, 32);
        return orgItemView;
    }

    @Nullable
    @Override
    public String getItem(int position) {
        return super.getItem(position-1);
    }

    private View inputItem(ViewGroup parent) {
        final ImageView inputItem = new ImageView(parent.getContext());
        int padding = ((int) new DP(getContext(), 16).px());
        inputItem.setPadding(padding, padding, padding, padding);
        inputItem.setLayoutParams(new ViewGroup.LayoutParams(
            MATCH_PARENT,
            parent.getWidth() / 4));
        inputItem.setImageResource(R.drawable.ic_input);
        return inputItem;
    }

    private View removalItem(ViewGroup parent) {
        final ImageView itemRemove = new ImageView(parent.getContext());
        int padding = ((int) new DP(getContext(), 16).px());
        itemRemove.setPadding(padding, padding, padding, padding);
        itemRemove.setLayoutParams(new ViewGroup.LayoutParams(
            MATCH_PARENT,
            parent.getWidth() / 4));
        itemRemove.setImageResource(R.drawable.ic_cross);
        return itemRemove;
    }

    @Override
    public int getCount() {
        return super.getCount() + 2;
    }
}
