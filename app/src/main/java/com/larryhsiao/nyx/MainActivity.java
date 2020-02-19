package com.larryhsiao.nyx;

import android.os.Bundle;
import androidx.annotation.Nullable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.larryhsiao.nyx.android.jot.JotListFragment;
import com.larryhsiao.nyx.android.jot.JotMapFragment;
import com.larryhsiao.nyx.migration.MigrateFromV1;
import com.silverhetch.aura.AuraActivity;

/**
 * Entry Activity of Nyx.
 */
public class MainActivity extends AuraActivity {
    private BottomNavigationView navigation;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.page_main);
        setupPageControl(R.id.main_page_container);
        navigation = findViewById(R.id.main_navigation);
        navigation.setOnNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.menuItem_jots) {
                rootPage(new JotListFragment());
                return true;
            } else if (item.getItemId() == R.id.menuItem_map) {
                rootPage(new JotMapFragment());
                return true;
            } else if (item.getItemId() == R.id.menuItem_calendar) {
                rootPage(new CalendarFragment());
                return true;
            } else {
                return false;
            }
        });

        if (savedInstanceState == null) {
            navigation.setSelectedItemId(R.id.menuItem_jots);
            rootPage(new JotListFragment());
        }

        new MigrateFromV1(((JotApplication) getApplication()).db, this).fire();
    }
}
