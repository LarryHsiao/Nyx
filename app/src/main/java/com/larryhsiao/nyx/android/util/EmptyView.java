package com.larryhsiao.nyx.android.util;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import com.larryhsiao.nyx.R;
import com.silverhetch.clotho.Source;

/**
 * Source to build empty view.
 */
public class EmptyView implements Source<View> {
    private final Context context;

    public EmptyView(Context context) {
        this.context = context;
    }

    @Override
    public View value() {
        final TextView emptyView = new TextView(context);
        emptyView.setGravity(Gravity.CENTER);
        emptyView.setCompoundDrawablesWithIntrinsicBounds(
            0, R.drawable.ic_black_hole, 0, 0
        );
        emptyView.setTextAppearance(R.style.TextAppearance_AppCompat_Title);
        emptyView.setText(R.string.empty);
        return emptyView;
    }
}
