package com.larryhsiao.nyx.android.jot;

import android.content.Intent;
import android.os.Bundle;
import android.view.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.larryhsiao.nyx.R;
import com.larryhsiao.nyx.android.base.JotFragment;
import com.larryhsiao.nyx.jots.*;

import java.util.List;

import static android.app.Activity.RESULT_OK;

/**
 * Fragment for showing Jot list.
 */
public class JotListFragment extends JotFragment {
    private static final String ARG_JOT_IDS = "ARG_JOT_IDS";
    private static final int REQUEST_CODE_CREATE_JOT = 1000;
    private static final int REQUEST_CODE_JOT_CONTENT = 1001;
    private JotListAdapter adapter;

    /**
     * Show by jot ids.
     */
    public static Fragment newInstanceByJotIds(long[] jotIds) {
        Fragment frag = new JotListFragment();
        Bundle args = new Bundle();
        args.putLongArray(ARG_JOT_IDS, jotIds);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        setTitle(getString(R.string.jots));
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final RecyclerView list = view.findViewById(R.id.list);
        list.setLayoutManager(new LinearLayoutManager(view.getContext()));
        list.setAdapter(adapter = new JotListAdapter(jot -> {
            Fragment frag = JotContentFragment.newInstance(jot.id());
            frag.setTargetFragment(this, REQUEST_CODE_JOT_CONTENT);
            nextPage(frag);
            return null;
        }));
        adapter.loadJots(loadJots());
    }

    private List<Jot> loadJots() {
        final Bundle args = getArguments();
        long[] jotIds = new long[0];
        if (args != null && args.getLongArray(ARG_JOT_IDS) != null) {
            jotIds = args.getLongArray(ARG_JOT_IDS);
        }
        if (jotIds.length != 0) {
            return new QueriedJots(new JotsByIds(db, jotIds)).value();
        } else {
            return new QueriedJots(new AllJots(db)).value();
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.jot_list, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menuItem_newJot) {
            Fragment frag = new NewJotFragment();
            frag.setTargetFragment(this, REQUEST_CODE_CREATE_JOT);
            nextPage(frag);
            return true;
        }
        return false;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_CREATE_JOT) {
            if (resultCode == RESULT_OK) {
                adapter.insertJot(new JotById(new JotUriId(data.getData().toString()).value(), db).value());
                getFragmentManager().popBackStack();
            }
        } else if (requestCode == REQUEST_CODE_JOT_CONTENT) {
            if (resultCode == RESULT_OK) {
                adapter.updateJot(new JotById(new JotUriId(data.getData().toString()).value(), db).value());
                getFragmentManager().popBackStack();
            }
        }
    }
}
