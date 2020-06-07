package com.larryhsiao.nyx.jot;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.larryhsiao.nyx.R;
import com.larryhsiao.nyx.core.jots.*;
import com.larryhsiao.nyx.core.jots.filter.Filter;
import com.larryhsiao.nyx.util.EmptyView;
import com.silverhetch.aura.view.fab.FabBehavior;
import com.silverhetch.aura.view.recyclerview.EmptyListAdapter;

import java.util.stream.Collectors;

import static android.app.Activity.RESULT_OK;
import static java.util.Arrays.stream;

/**
 * Fragment for showing Jot list.
 */
public class JotListFragment extends JotListingFragment {
    private static final String ARG_JOT_IDS = "ARG_JOT_IDS";
    private static final String ARG_TITLE = "ARG_TITLE";
    private static final int REQUEST_CODE_CREATE_JOT = 1000;
    private static final int REQUEST_CODE_JOT_CONTENT = 1001;
    private JotListAdapter adapter;

    public static Fragment newInstance(JotListingFragment listingFragment) {
        JotListFragment frag = new JotListFragment();
        Bundle bundle = new Bundle();
        listingFragment.setupFilterArgs(bundle);
        frag.setArguments(bundle);
        return frag;
    }

    public JotListFragment() {
        setArguments(new Bundle());
    }

    /**
     * Show by jot ids.
     */
    public static Fragment newInstanceByJotIds(String title, long[] jotIds) {
        Fragment frag = new JotListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TITLE, title);
        args.putLongArray(ARG_JOT_IDS, jotIds);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(requireArguments().getString(ARG_TITLE, getString(R.string.jots)));
        adapter = new JotListAdapter(db, jot -> {
            Fragment frag = JotContentFragment.newInstance(new ConstJot(jot));
            frag.setTargetFragment(this, REQUEST_CODE_JOT_CONTENT);
            nextPage(frag);
        });
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
        list.setAdapter(new EmptyListAdapter(adapter, new EmptyView(view.getContext())));
        loadJots();
    }

    @Override
    protected void loadJots(Filter filter) {
        final Bundle args = getArguments();
        final long[] jotIds;
        if (args != null && args.getLongArray(ARG_JOT_IDS) != null) {
            jotIds = args.getLongArray(ARG_JOT_IDS);
        } else {
            jotIds = new long[0];
        }
        if (args != null && jotIds != null && jotIds.length > 0) {
            adapter.loadJots(
                new QueriedJots(new JotsByCheckedFilter(db, filter))
                    .value()
                    .stream()
                    .filter(it -> stream(jotIds).anyMatch(value -> it.id() == value))
                    .collect(Collectors.toList())
            );
        } else {
            adapter.loadJots(new QueriedJots(new JotsByCheckedFilter(db, filter)).value());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        attachFab(new FabBehavior() {
            @Override
            public int icon() {
                return R.drawable.ic_plus;
            }

            @Override
            public void onClick() {
                Fragment frag = JotContentFragment.newInstance();
                frag.setTargetFragment(JotListFragment.this, REQUEST_CODE_CREATE_JOT);
                nextPage(frag);
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        detachFab();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_CREATE_JOT && resultCode == RESULT_OK && data != null) {
            adapter.insertJot(new JotById(new JotUriId(data.getData().toString()).value(), db).value());
            getParentFragmentManager().popBackStack();
        } else if (requestCode == REQUEST_CODE_JOT_CONTENT && resultCode == RESULT_OK && data != null) {
            adapter.updateJot(new JotById(new JotUriId(data.getData().toString()).value(), db).value());
            getParentFragmentManager().popBackStack();
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menuItem_viewMode) {
            rootPage(JotMapFragment.newInstance(this));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
