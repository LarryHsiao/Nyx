package com.larryhsiao.nyx.view.internals;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

/**
 * Activity that can browse internal files in Nyx.
 */
public class BrowserActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final FrameLayout root = new FrameLayout(this);
        root.setLayoutParams(new ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT));
        root.setId(View.generateViewId());
        setContentView(root);
        getSupportFragmentManager().beginTransaction()
                .replace(
                        root.getId(),
                        BrowseFragment.newInstance(getFilesDir().getAbsolutePath())
                ).commit();
    }
}
