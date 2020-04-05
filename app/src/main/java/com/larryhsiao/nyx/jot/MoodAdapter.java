package com.larryhsiao.nyx.jot;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import com.larryhsiao.nyx.R;
import com.silverhetch.aura.view.measures.DP;

import static android.util.TypedValue.COMPLEX_UNIT_DIP;
import static android.view.Gravity.CENTER;
import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

public class MoodAdapter extends ArrayAdapter<String> {

    public MoodAdapter(@NonNull Context context) {
        super(context,
            android.R.layout.simple_list_item_1,
            new String[]{
                "x",
                new String(Character.toChars(0x1F603)),
                new String(Character.toChars(0x1F601)),
                new String(Character.toChars(0x1F602)),
                new String(Character.toChars(0x1F642)),
                new String(Character.toChars(0x1F970)),
                new String(Character.toChars(0x1F60D)),
                new String(Character.toChars(0x1F60B)),
                new String(Character.toChars(0x1F60F)),
                new String(Character.toChars(0x1F612)),
                new String(Character.toChars(0x1F928)),
                new String(Character.toChars(0x1F611)),
                new String(Character.toChars(0x1F614)),
                new String(Character.toChars(0x1F634)),
                new String(Character.toChars(0x1F912)),
                new String(Character.toChars(0x1F927)),
                new String(Character.toChars(0x1F976)),
                new String(Character.toChars(0x1F974)),
                new String(Character.toChars(0x1F973)),
                "",
            }
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
}
