package com.larryhsiao.nyx.android.jot;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import com.haibin.calendarview.Calendar;
import com.haibin.calendarview.CalendarView;
import com.larryhsiao.nyx.R;
import com.larryhsiao.nyx.android.base.JotFragment;
import com.larryhsiao.nyx.jots.AllJots;
import com.larryhsiao.nyx.jots.Jot;
import com.larryhsiao.nyx.jots.JotsByDate;
import com.larryhsiao.nyx.jots.QueriedJots;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import static android.app.Activity.RESULT_OK;

/**
 * Calendar for viewing Jots
 */
public class CalendarFragment extends JotFragment {
    private static final int REQUEST_CODE_DIARY_CONTENT = 1000;
    private CalendarView calendarView;
    private JotListAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.page_calendar, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        calendarView = view.findViewById(R.id.calendar_calendarView);
        RecyclerView jotList = view.findViewById(R.id.calendar_list);
        adapter = new JotListAdapter(item -> {
            Fragment frag = JotContentFragment.newInstance(item.id());
            frag.setTargetFragment(this, REQUEST_CODE_DIARY_CONTENT);
            nextPage(frag);
            return null;
        });
        jotList.setAdapter(adapter);
        loadJotsByDate(java.util.Calendar.getInstance());
        setTitle(dateString());
        List<Jot> jots = new QueriedJots(new AllJots(db)).value();
        for (Jot jot : jots) {
            final java.util.Calendar jdkCalendar = java.util.Calendar.getInstance(TimeZone.getTimeZone("UTC"));
            jdkCalendar.setTime(new Date(jot.createdTime()));

            final Calendar calendar = new Calendar();
            calendar.setYear(jdkCalendar.get(java.util.Calendar.YEAR));
            calendar.setMonth(jdkCalendar.get(java.util.Calendar.MONTH) + 1);
            calendar.setDay(jdkCalendar.get(java.util.Calendar.DAY_OF_MONTH));
            calendar.setScheme("*");
            calendar.setSchemeColor(Color.BLACK);
            calendarView.addSchemeDate(calendar);
        }
        calendarView.setOnCalendarSelectListener(new CalendarView.OnCalendarSelectListener() {
            @Override
            public void onCalendarOutOfRange(Calendar calendar) {
                // Do nothing
            }

            @Override
            public void onCalendarSelect(Calendar calendar, boolean isClick) {
                setTitle(dateString());
                java.util.Calendar jdkCalendar = java.util.Calendar.getInstance();
                jdkCalendar.set(
                    calendar.getYear(),
                    calendar.getMonth() - 1,
                    calendar.getDay()
                );
                loadJotsByDate(jdkCalendar);
            }
        });
    }

    private String dateString() {
        Calendar selected = calendarView.getSelectedCalendar();
        java.util.Calendar date = java.util.Calendar.getInstance();
        date.set(
            selected.getYear(),
            selected.getMonth() - 1,
            selected.getDay()
        );
        return DateFormat.getDateInstance().format(date.getTime());
    }

    private void loadJotsByDate(java.util.Calendar jdkCalendar) {
        adapter.loadJots(
            new QueriedJots(new JotsByDate(new java.sql.Date(jdkCalendar.getTimeInMillis()), db)).value()
        );
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_DIARY_CONTENT && resultCode == RESULT_OK) {
            getFragmentManager().popBackStack();
        }
    }
}
