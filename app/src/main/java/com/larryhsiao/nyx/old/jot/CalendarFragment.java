package com.larryhsiao.nyx.old.jot;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import com.haibin.calendarview.Calendar;
import com.haibin.calendarview.CalendarLayout;
import com.haibin.calendarview.CalendarView;
import com.larryhsiao.nyx.R;
import com.larryhsiao.nyx.core.jots.*;
import com.larryhsiao.nyx.old.base.JotFragment;
import com.larryhsiao.nyx.old.util.EmptyView;
import com.larryhsiao.aura.view.fab.FabBehavior;
import com.larryhsiao.aura.view.recyclerview.EmptyListAdapter;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;

import static android.app.Activity.RESULT_OK;
import static java.lang.Double.MIN_VALUE;

/**
 * Calendar for viewing Jots.
 */
public class CalendarFragment extends JotFragment {
    private static final int REQUEST_CODE_DIARY_CONTENT = 1000;
    private static final int REQUEST_CODE_NEW_JOT = 1001;
    private CalendarView calendarView;
    private RecyclerView jotList;
    private JotListAdapter adapter;
    private CalendarLayout root;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.page_calendar, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        root = getView().findViewById(R.id.calendar_layout);
        calendarView = view.findViewById(R.id.calendar_calendarView);
        jotList = view.findViewById(R.id.calendar_list);
        adapter = new JotListAdapter(db, item -> {
            Fragment frag = JotContentFragment.newInstance(new ConstJot(item));
            frag.setTargetFragment(this, REQUEST_CODE_DIARY_CONTENT);
            nextPage(frag);
        });
        jotList.setAdapter(new EmptyListAdapter(adapter, new EmptyView(view.getContext())));
        setTitle(dateString());
        List<Jot> jots = new QueriedJots(new AllJots(db)).value();
        for (Jot jot : jots) {
            final java.util.Calendar jdkCalendar = java.util.Calendar.getInstance();
            jdkCalendar.setTime(new Date(jot.createdTime()));

            final Calendar calendar = new Calendar();
            calendar.setYear(jdkCalendar.get(java.util.Calendar.YEAR));
            calendar.setMonth(jdkCalendar.get(java.util.Calendar.MONTH) + 1);
            calendar.setDay(jdkCalendar.get(java.util.Calendar.DAY_OF_MONTH));
            calendar.setScheme(jot.mood());
            calendar.setSchemeColor(Color.CYAN);
            calendarView.addSchemeDate(calendar);
            calendarView.addSchemeDate(calendar);
        }
        calendarView.setOnCalendarSelectListener(new CalendarView.OnCalendarSelectListener() {
            @Override
            public void onCalendarOutOfRange(Calendar calendar) {
                // Do nothing
            }

            @Override
            public void onCalendarSelect(Calendar calendar, boolean isClick) {
                java.util.Calendar date = selectedDate();
                setTitle(dateString(date));
                loadJotsByDate(date);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        loadJotsByDate();
        attachFab(new FabBehavior() {
            @Override
            public int icon() {
                return R.drawable.ic_plus;
            }

            @Override
            public void onClick() {
                Fragment frag = JotContentFragment.newInstance(new ConstJot(
                    -1,
                    "",
                    "",
                    selectedDate().getTimeInMillis(),
                    new double[]{MIN_VALUE, MIN_VALUE},
                    "",
                    1,
                    false
                ));
                frag.setTargetFragment(CalendarFragment.this, REQUEST_CODE_NEW_JOT);
                nextPage(frag);
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        detachFab();
    }

    private java.util.Calendar selectedDate() {
        Calendar selected = calendarView.getSelectedCalendar();
        java.util.Calendar date = java.util.Calendar.getInstance();
        date.set(
            selected.getYear(),
            selected.getMonth() - 1,
            selected.getDay()
        );
        return date;
    }

    private String dateString() {
        return dateString(selectedDate());
    }

    private String dateString(java.util.Calendar date) {
        return DateFormat.getDateInstance().format(date.getTime());
    }

    private void loadJotsByDate() {
        loadJotsByDate(selectedDate());
    }

    private void loadJotsByDate(java.util.Calendar jdkCalendar) {
        adapter.loadJots(
            new QueriedJots(new JotsByDate(new java.sql.Date(jdkCalendar.getTimeInMillis()), db)).value()
        );
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_DIARY_CONTENT && resultCode == RESULT_OK) {
            getFragmentManager().popBackStack();
        }
        if (requestCode == REQUEST_CODE_NEW_JOT && resultCode == RESULT_OK) {
            loadJotsByDate();
            getFragmentManager().popBackStack();
            new Handler().postDelayed(() -> {
                root.shrink();
                if (adapter.getItemCount() > 0) {
                    jotList.smoothScrollToPosition(adapter.getItemCount() - 1);
                }
            }, 100);
        }
    }
}
