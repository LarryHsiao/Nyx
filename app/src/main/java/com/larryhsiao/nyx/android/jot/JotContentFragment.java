package com.larryhsiao.nyx.android.jot;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.*;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.larryhsiao.nyx.R;
import com.larryhsiao.nyx.android.base.JotFragment;
import com.larryhsiao.nyx.jots.*;

import static android.app.Activity.RESULT_OK;

/**
 * Fragment that shows the Jot content.
 */
public class JotContentFragment extends JotFragment {
    private static final String ARG_JOT_ID = "ARG_JOT_ID";
    private EditText contentEditText;
    private Jot jot;

    public static Fragment newInstance(long jotId) {
        final Fragment frag = new JotContentFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_JOT_ID, jotId);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        jot = new JotById(getArguments().getLong(ARG_JOT_ID), db).value();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.page_jot, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        contentEditText = view.findViewById(R.id.newJot_content);
        contentEditText.setText(jot.content());
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.jot_edit, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menuItem_save) {
            new UpdateJot(new UpdatedJot(
                    jot,
                    contentEditText.getText().toString()
            ), db).fire();
            final Intent intent = new Intent();
            intent.setData(Uri.parse(new JotUri(
                            jot
                    ).value().toASCIIString()));
            sendResult(0, RESULT_OK, intent);
            return true;
        }
        return false;
    }
}
