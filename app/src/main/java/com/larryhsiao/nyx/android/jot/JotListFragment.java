package com.larryhsiao.nyx.android.jot;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.larryhsiao.nyx.R;
import com.larryhsiao.nyx.android.base.JotFragment;
import com.larryhsiao.nyx.android.util.EmptyView;
import com.larryhsiao.nyx.jots.AllJots;
import com.larryhsiao.nyx.jots.ConstJot;
import com.larryhsiao.nyx.jots.Jot;
import com.larryhsiao.nyx.jots.JotById;
import com.larryhsiao.nyx.jots.JotUriId;
import com.larryhsiao.nyx.jots.JotsByIds;
import com.larryhsiao.nyx.jots.JotsByKeyword;
import com.larryhsiao.nyx.jots.QueriedJots;
import com.silverhetch.aura.view.EmptyListAdapter;

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
        final Bundle args = getArguments();
        setHasOptionsMenu(args == null || args.getLongArray(ARG_JOT_IDS) == null);
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
        adapter = new JotListAdapter(db, jot -> {
            Fragment frag = JotContentFragment.newInstance(new ConstJot(jot));
            frag.setTargetFragment(this, REQUEST_CODE_JOT_CONTENT);
            nextPage(frag);
            return null;
        });
        final RecyclerView list = view.findViewById(R.id.list);
        list.setLayoutManager(new LinearLayoutManager(view.getContext()));
        list.setAdapter(new EmptyListAdapter(adapter, new EmptyView(view.getContext())));
        adapter.loadJots(loadJotsByArg());
    }

    private List<Jot> loadJotsByArg() {
        final Bundle args = getArguments();
        long[] jotIds = new long[0];
        if (args != null && args.getLongArray(ARG_JOT_IDS) != null) {
            jotIds = args.getLongArray(ARG_JOT_IDS);
        }
        if (args != null && args.getLongArray(ARG_JOT_IDS) != null) {
            return new QueriedJots(new JotsByIds(db, jotIds)).value();
        } else {
            return new QueriedJots(new AllJots(db)).value();
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.jot_list, menu);

        SearchManager searchManager = ((SearchManager) getContext().getSystemService(Context.SEARCH_SERVICE));
        MenuItem searchMenuItem = menu.findItem(R.id.menuItem_search);
        SearchView searchView = (SearchView) searchMenuItem.getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
        searchView.setOnCloseListener(() -> {
            searchMenuItem.collapseActionView();
            return false;
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.loadJots(new QueriedJots(new JotsByKeyword(db, newText)).value());
                return true;
            }
        });
        searchMenuItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                searchView.setQuery("", true);
                return true;
            }
        });
        searchMenuItem.setOnMenuItemClickListener(item -> {
            searchView.onActionViewExpanded();
            return false;
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menuItem_newJot) {
            Fragment frag = new JotContentFragment();
            frag.setTargetFragment(this, REQUEST_CODE_CREATE_JOT);
            nextPage(frag);
            return true;
        }
        return false;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_CREATE_JOT && resultCode == RESULT_OK) {
            adapter.insertJot(new JotById(new JotUriId(data.getData().toString()).value(), db).value());
            getFragmentManager().popBackStack();
        } else if (requestCode == REQUEST_CODE_JOT_CONTENT && resultCode == RESULT_OK) {
            adapter.updateJot(new JotById(new JotUriId(data.getData().toString()).value(), db).value());
            getFragmentManager().popBackStack();
        }
    }
}
