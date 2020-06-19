package com.larryhsiao.nyx.jot;

import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.*;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.haibin.calendarview.Calendar;
import com.haibin.calendarview.CalendarView;
import com.larryhsiao.nyx.R;
import com.larryhsiao.nyx.base.JotFragment;
import com.larryhsiao.nyx.core.jots.filter.ConstFilter;
import com.larryhsiao.nyx.core.jots.filter.Filter;
import com.larryhsiao.nyx.core.jots.filter.WrappedFilter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static android.content.Context.SEARCH_SERVICE;
import static android.graphics.Color.WHITE;
import static java.text.DateFormat.SHORT;
import static java.util.Calendar.*;

/**
 * Base fragment for jot listing. implements the common functions.
 */
abstract class JotListingFragment extends JotFragment {
    private static final String ARG_FILTER = "ARG_FILTER";
    private Filter filter = new ConstFilter(new long[]{0L, 0L}, "");

    protected void setupFilterArgs(Bundle args) {
        Gson gson = new GsonBuilder()
            .registerTypeAdapter(Filter.class, new FilterTypeAdapter())
            .create();
        args.putString(
            ARG_FILTER,
            gson.toJson(
                filter, new TypeToken<Filter>() {
                }.getType()
            )
        );
    }

    protected abstract void loadJots(Filter filter);

    private void initialFilter() {
        try {
            String json = requireArguments().getString(ARG_FILTER, "");
            if (json.isEmpty()){
                filter = new WrappedFilter(filter){
                    @Override
                    public long[] dateRange() {
                        return new long[]{
                            System.currentTimeMillis()-2592000000L, // 30-day
                            System.currentTimeMillis()
                        };
                    }
                };
            }else{
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
            filter = new ConstFilter(new long[]{0L, 0L}, "");
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
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.jot_list, menu);

        SearchManager searchManager = ((SearchManager) requireContext().getSystemService(SEARCH_SERVICE));
        MenuItem searchMenuItem = menu.findItem(R.id.menuItem_search);
        SearchView searchView = (SearchView) searchMenuItem.getActionView();
        searchView.setSearchableInfo(
            searchManager.getSearchableInfo(requireActivity().getComponentName())
        );
        searchView.setOnCloseListener(() -> {
            searchMenuItem.collapseActionView();
            return false;
        });
        searchView.postDelayed(()->{
            searchView.setQuery(filter.keyword(), false);
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    new TagSuggestion(db, newText, searchView).fire();
                    filter = new WrappedFilter(filter) {
                        @Override
                        public String keyword() {
                            return newText;
                        }
                    };
                    loadJots(filter);
                    return true;
                }
            });
        },300);
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
            menu.findItem(R.id.menuItem_datePicker).setIconTintList(
                ColorStateList.valueOf(WHITE)
            );
        } else {
            menu.findItem(R.id.menuItem_datePicker).setIconTintList(
                ColorStateList.valueOf(getResources().getColor(
                    R.color.colorActive
                ))
            );
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menuItem_datePicker) {
            View root = LayoutInflater.from(getContext()).inflate(R.layout.dialog_date_picker, null , false);
            CalendarView view = root.findViewById(R.id.datePicker_calendar);
            AlertDialog dialogBuilder = new AlertDialog.Builder(requireContext())
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
                    filter = new WrappedFilter(filter) {
                        @Override
                        public long[] dateRange() {
                            return new long[]{started, ended};
                        }
                    };
                    loadJots(filter);
                    item.setIconTintList(ColorStateList.valueOf(
                        getResources().getColor(R.color.colorActive)
                    ));
                })
                .setNeutralButton(R.string.Clear, (dialog, which) -> {
                    filter = new WrappedFilter(filter) {
                        @Override
                        public long[] dateRange() {
                            return new long[]{0L, 0L};
                        }
                    };
                    loadJots(filter);
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
                    final DateFormat formatter = SimpleDateFormat.getDateInstance(SHORT);
                    final TextView selected = root.findViewById(R.id.datePicker_selected);
                    if (isEnd) {
                        List<Calendar> range = view.getSelectCalendarRange();
                        selected.setText(String.format(
                            "%s~%s",
                            formatter.format(new Date(range.get(0).getTimeInMillis())),
                            formatter.format(
                                new Date(range.get(range.size() - 1).getTimeInMillis())
                            )
                        ));
                    } else {
                        selected.setText(
                            formatter.format(new Date(
                                view.getSelectedCalendar().getTimeInMillis())
                            )
                        );
                    }
                }
            });
            TextView yearText = root.findViewById(R.id.datePicker_year);
            yearText.setText(String.valueOf(view.getCurYear()));
            yearText.setOnClickListener(v ->
                view.showYearSelectLayout(view.getCurYear())
            );
            view.setOnYearChangeListener(year ->
                yearText.setText(String.valueOf(year))
            );
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
                if (filter.dateRange()[0] == filter.dateRange()[1]) {
                    dialogBuilder.setTitle(
                        SimpleDateFormat.getDateInstance(SHORT).format(
                            new Date(filter.dateRange()[0])
                        )
                    );
                }
            }
            return true;
        }
        return false;
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
