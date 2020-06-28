package com.larryhsiao.nyx.tag;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.*;
import android.widget.ArrayAdapter;
import android.widget.SearchView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.larryhsiao.nyx.R;
import com.larryhsiao.nyx.base.JotFragment;
import com.larryhsiao.nyx.core.jots.Jot;
import com.larryhsiao.nyx.core.jots.QueriedJots;
import com.larryhsiao.nyx.core.tags.*;
import com.larryhsiao.nyx.jot.JotListFragment;
import com.larryhsiao.nyx.util.EmptyView;
import com.silverhetch.aura.view.dialog.InputDialog;
import com.silverhetch.aura.view.fab.FabBehavior;
import com.silverhetch.aura.view.recyclerview.EmptyListAdapter;
import com.silverhetch.clotho.source.ConstSource;
import com.silverhetch.clotho.utility.comparator.StringComparator;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static android.app.Activity.RESULT_OK;

/**
 * Fragment to showing/manage tags.
 *
 * @todo #0 remove this page, and add mutation function for each page have tag list.
 */
public class TagListFragment extends JotFragment {
    private static final int REQUEST_CODE_NEW_TAG = 1000;
    private TagListAdapter adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        adapter = new TagListAdapter(tag -> {
            nextPage(
                JotListFragment.newInstanceByJotIds(
                    getString(R.string.tag_title, tag.title()),
                    new QueriedJots(new JotsByTagId(db, new ConstSource<>(tag.id())))
                        .value().stream().mapToLong(Jot::id).toArray(),
                    null
                )
            );
            return new Object();
        }, tag -> {
            final boolean canCombine = adapter.tags().size() > 1;
            final ArrayList<String> options = new ArrayList<>();
            if (canCombine) {
                options.add(getString(R.string.combine));
            }
            options.add(getString(R.string.delete));

            new AlertDialog.Builder(view.getContext())
                .setTitle(tag.title())
                .setAdapter(new ArrayAdapter<>(
                    view.getContext(),
                    android.R.layout.simple_list_item_1,
                    options
                ), (dialog, which) -> {
                    if (which == (canCombine ? 1 : 0)) {
                        new TagRemoval(db, tag.id()).fire();
                        adapter.removeTag(tag);
                    } else if (canCombine && which == 0) {
                        combine(tag);
                    }
                }).show();
            return new Object();
        });
        final RecyclerView list = view.findViewById(R.id.list);
        list.setLayoutManager(new LinearLayoutManager(view.getContext()));
        list.setAdapter(new EmptyListAdapter(adapter, new EmptyView(view.getContext())));
        adapter.loadTags(
            new QueriedTags(
                new AllTags(db)
            ).value().stream().sorted(new Comparator<Tag>() {
                final StringComparator comparator = new StringComparator();

                @Override
                public int compare(Tag o1, Tag o2) {
                    return comparator.compare(o2.title(), o1.title());
                }
            }).collect(Collectors.toList())
        );
    }

    private void combine(Tag targetTag) {
        final List<Tag> otherTags = adapter.tags()
            .stream()
            .filter(filterTag -> targetTag.id() != filterTag.id())
            .collect(Collectors.toList());
        new AlertDialog.Builder(getContext())
            .setTitle(getString(R.string.combine_, targetTag.title()))
            .setAdapter(new ArrayAdapter<>(
                    getContext(),
                    android.R.layout.simple_list_item_1,
                    otherTags.stream()
                        .map(mapTag -> mapTag.title())
                        .collect(Collectors.toList())
                ), (dialog, which) -> {
                    new CombineTags(
                        db,
                        targetTag.id(),
                        otherTags.get(which).id()).fire();
                    adapter.removeTag(otherTags.get(which));
                }
            ).show();
    }

    @Override
    public void onResume() {
        super.onResume();
        setTitle(getString(R.string.tags));
        attachFab(new FabBehavior() {
            @Override
            public int icon() {
                return R.drawable.ic_plus;
            }

            @Override
            public void onClick() {
                final InputDialog frag = InputDialog.Companion.newInstance(
                    getString(R.string.new_tag),
                    REQUEST_CODE_NEW_TAG
                );
                frag.setTargetFragment(TagListFragment.this, REQUEST_CODE_NEW_TAG);
                frag.show(getFragmentManager(), null);
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        detachFab();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.jot_list, menu);
        menu.findItem(R.id.menuItem_viewMode).setVisible(false);

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
                StringComparator comparator = new StringComparator();
                adapter.loadTags(
                    new QueriedTags(new TagsByKeyword(db, newText))
                        .value()
                        .stream()
                        .sorted((o1, o2) ->
                            comparator.compare(o2.title(), o1.title())
                        ).collect(Collectors.toList())
                );
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
    public void onActivityResult(int requestCode, int resultCode, @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (REQUEST_CODE_NEW_TAG == requestCode && RESULT_OK == resultCode) {
            final String newTagName = data.getStringExtra("INPUT_FIELD");
            adapter.appendTag(new NewTag(db, newTagName).value());
        }
    }
}
