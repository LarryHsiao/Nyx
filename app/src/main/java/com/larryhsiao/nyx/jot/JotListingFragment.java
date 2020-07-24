package com.larryhsiao.nyx.jot;

import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.*;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.haibin.calendarview.Calendar;
import com.haibin.calendarview.CalendarView;
import com.larryhsiao.nyx.R;
import com.larryhsiao.nyx.base.JotFragment;
import com.larryhsiao.nyx.core.jots.Jot;
import com.larryhsiao.nyx.core.jots.JotsByCheckedFilter;
import com.larryhsiao.nyx.core.jots.QueriedJots;
import com.larryhsiao.nyx.core.jots.filter.ConstFilter;
import com.larryhsiao.nyx.core.jots.filter.Filter;
import com.larryhsiao.nyx.core.jots.filter.WrappedFilter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static android.content.Context.SEARCH_SERVICE;
import static android.graphics.Color.WHITE;
import static com.larryhsiao.nyx.NyxActions.SYNC_CHECKPOINT;
import static java.text.DateFormat.SHORT;
import static java.util.Arrays.stream;
import static java.util.Calendar.*;

/**
 * Base fragment for jot listing. implements the common functions.
 */
abstract class JotListingFragment extends JotFragment {
    private static final String ARG_FILTER = "ARG_FILTER";
    private Filter filter = new ConstFilter();
    private final BroadcastReceiver jotChanged = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            loadJots();
        }
    };

    protected void setupFilterArgs(Bundle args) {
        setupFilterArgs(args, filter);
    }

    protected void setupFilterArgs(Bundle args, long[] jots) {
        setupFilterArgs(args, new WrappedFilter(filter) {
            @Override
            public long[] ids() { return jots; }
        });
    }

    protected static void setupFilterArgs(Bundle args, Filter filter) {
        Gson gson = new GsonBuilder()
            .registerTypeAdapter(Filter.class, new FilterTypeAdapter())
            .create();
        args.putString(
            ARG_FILTER,
            gson.toJson(
                filter,
                new TypeToken<Filter>() {}.getType()
            )
        );
    }

    protected void onPreUpdateFilter(Filter filter) { }

    protected void onUpdateFilter(Filter newFilter) {
        onPreUpdateFilter(filter);
        this.filter = newFilter;
        loadJots(filter);
    }

    protected abstract void loadJots(List<Jot> jots);

    private void loadJots(Filter filter) {
        loadJots(
            new QueriedJots(new JotsByCheckedFilter(db, filter))
                .value()
                .stream()
                .filter(it -> filter.ids().length == 0 ||
                    stream(filter.ids()).anyMatch(value -> it.id() == value)
                )
                .collect(Collectors.toList())
        );
    }

    private void initialFilter() {
        try {
            String json = requireArguments().getString(ARG_FILTER, "");
            if (!json.isEmpty()) {
                Gson gson = new GsonBuilder()
                    .registerTypeAdapter(Filter.class, new FilterTypeAdapter())
                    .create();
                filter = gson.fromJson(
                    json,
                    new TypeToken<Filter>() {
                    }.getType()
                );
            }
        } catch (Exception e) {
            filter = new ConstFilter();
        }
    }

    protected void loadJots() {
        loadJots(filter);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        initialFilter();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        LocalBroadcastManager.getInstance(view.getContext()).registerReceiver(
            jotChanged, new IntentFilter(SYNC_CHECKPOINT)
        );
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(jotChanged);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.jot_list, menu);

        SearchManager searchManager =
            ((SearchManager) requireContext().getSystemService(SEARCH_SERVICE));
        MenuItem searchMenuItem = menu.findItem(R.id.menuItem_search);
        SearchView searchView = (SearchView) searchMenuItem.getActionView();
        searchView.setSearchableInfo(
            searchManager.getSearchableInfo(requireActivity().getComponentName())
        );
        searchView.setOnCloseListener(() -> {
            searchMenuItem.collapseActionView();
            return false;
        });
        searchView.postDelayed(() -> {
            searchView.setQuery(filter.keyword(), false);
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    new TagSuggestion(db, newText, searchView).fire();
                    onUpdateFilter(new WrappedFilter(filter) {
                        @Override
                        public String keyword() {
                            return newText;
                        }
                    });
                    return true;
                }
            });
        }, 300);
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
        if (filter.dateRange()[0] == 0L && filter.dateRange()[0] == 0L) {
            menu.findItem(R.id.menuItem_datePicker).setIconTintList(ColorStateList.valueOf(WHITE));
        } else {
            menu.findItem(R.id.menuItem_datePicker).setIconTintList(
                ColorStateList.valueOf(getResources().getColor(R.color.colorActive))
            );
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menuItem_datePicker) {
            View root =
                LayoutInflater.from(getContext()).inflate(R.layout.dialog_date_picker, null, false);
            CalendarView view = root.findViewById(R.id.datePicker_calendar);
            new AlertDialog.Builder(requireContext())
                .setView(root)
                .setPositiveButton(R.string.confirm, (dialog, which) -> {
                    final long started;
                    final long ended;
                    if (view.getSelectCalendarRange().size() == 0) {
                        started = view.getSelectedCalendar().getTimeInMillis();
                        ended = started;
                    } else {
                        List<Calendar> range = view.getSelectCalendarRange();
                        started = range.get(0).getTimeInMillis();
                        ended = range.get(range.size() - 1).getTimeInMillis();
                    }
                    onUpdateFilter(new WrappedFilter(filter) {
                        @Override
                        public long[] dateRange() {
                            return new long[]{started, ended};
                        }
                    });
                    item.setIconTintList(ColorStateList.valueOf(
                        getResources().getColor(R.color.colorActive)
                    ));
                })
                .setNeutralButton(R.string.Clear, (dialog, which) -> {
                    onUpdateFilter(new WrappedFilter(filter) {
                        @Override
                        public long[] dateRange() {
                            return new long[]{0L, 0L};
                        }
                    });
                    item.setIconTintList(ColorStateList.valueOf(WHITE));
                })
                .setNegativeButton(R.string.cancel, (dialog, which) -> {
                    // Leave it not changed
                })
                .show();
            view.setMonthView(SelectRangeMonthView.class);
            view.setSelectRangeMode();
            view.setOnCalendarRangeSelectListener(new CalendarView.OnCalendarRangeSelectListener() {
                @Override
                public void onCalendarSelectOutOfRange(Calendar calendar) {

                }

                @Override
                public void onSelectOutOfRange(Calendar calendar, boolean isOutOfMinRange) {

                }

                @Override
                public void onCalendarRangeSelect(Calendar calendar, boolean isEnd) {
                    updateCalendarDialog(root, view);
                }
            });
            TextView yearText = root.findViewById(R.id.datePicker_year);
            yearText.setOnClickListener(v ->
                view.showYearSelectLayout(view.getSelectedCalendar().getYear())
            );
            view.setOnYearChangeListener(year -> yearText.setText(String.valueOf(year)));
            if (filter.dateRange()[0] == 0L && filter.dateRange()[1] == 0L) {
                view.setSelectCalendarRange(
                    calendarByDatetime(System.currentTimeMillis()),
                    calendarByDatetime(System.currentTimeMillis())
                );
            } else {
                view.setSelectCalendarRange(
                    calendarByDatetime(filter.dateRange()[0]),
                    calendarByDatetime(filter.dateRange()[1])
                );
            }
            yearText.setText(String.valueOf(view.getSelectedCalendar().getYear()));
            updateCalendarDialog(root, view);
            view.scrollToSelectCalendar();
            return true;
        }
        return false;
    }

    private void updateCalendarDialog(View root, CalendarView view) {
        final DateFormat formatter = SimpleDateFormat.getDateInstance(SHORT);
        final TextView selected = root.findViewById(R.id.datePicker_selected);
        if (view.getSelectCalendarRange() != null && view.getSelectCalendarRange().size() >= 2) {
            List<Calendar> range = view.getSelectCalendarRange();
            selected.setText(String.format(
                "%s~%s",
                formatter.format(new Date(range.get(0).getTimeInMillis())),
                formatter.format(new Date(range.get(range.size() - 1).getTimeInMillis()))
            ));
        } else {
            selected.setText(formatter.format(new Date(
                view.getSelectedCalendar().getTimeInMillis())
            ));
        }
    }

    private Calendar calendarByDatetime(long dateTime) {
        java.util.Calendar jdkCalendar = java.util.Calendar.getInstance();
        jdkCalendar.setTime(new Date(dateTime));
        Calendar calendar = new Calendar();
        calendar.setDay(jdkCalendar.get(DAY_OF_MONTH));
        calendar.setYear(jdkCalendar.get(YEAR));
        calendar.setMonth(jdkCalendar.get(MONTH) + 1);
        return calendar;
    }
}
