package com.larryhsiao.nyx.android.jot;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.*;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.larryhsiao.nyx.BuildConfig;
import com.larryhsiao.nyx.R;
import com.larryhsiao.nyx.android.base.JotFragment;
import com.larryhsiao.nyx.jots.Jot;
import com.larryhsiao.nyx.jots.JotUri;
import com.larryhsiao.nyx.jots.NewJot;

/**
 * Fragment to create new Jot.
 * <p>
 * Use this fragment with
 */
public class NewJotFragment extends JotFragment {
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        setTitle(getString(R.string.new_jot));
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.page_new_jot, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.new_jot, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menuItem_save) {
            EditText content = getView().findViewById(R.id.newJot_content);
            Jot newJot = new NewJot(db, content.getText().toString()).value();
            final Intent intent = new Intent();
            intent.setData(Uri.parse(new JotUri(BuildConfig.URI_HOST, newJot).value().toASCIIString()));
            sendResult(0, Activity.RESULT_OK, intent);
            return true;
        }
        return false;
    }
}

