package com.larryhsiao.nyx;

import android.os.Bundle;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.larryhsiao.nyx.account.AccountFragment;
import com.larryhsiao.nyx.base.JotActivity;
import com.larryhsiao.nyx.jot.CalendarFragment;
import com.larryhsiao.nyx.jot.JotListFragment;
import com.larryhsiao.nyx.jot.JotMapFragment;
import com.larryhsiao.nyx.tag.TagListFragment;

/**
 * Entry Activity of Nyx.
 */
public class MainActivity extends JotActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.page_main);
        setupPageControl(R.id.main_page_container);
        getSupportFragmentManager().addOnBackStackChangedListener(() ->
            getSupportActionBar().setDisplayHomeAsUpEnabled(getSupportFragmentManager().getBackStackEntryCount() > 0)
        );

        BottomNavigationView navigation = findViewById(R.id.main_navigation);
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
            } else if (item.getItemId() == R.id.menuItem_tag) {
                rootPage(new TagListFragment());
                return true;
            } else if (item.getItemId() == R.id.menuItem_account) {
                rootPage(new AccountFragment());
                return true;
            } else {
                return false;
            }
        });

        if (savedInstanceState == null) {
            navigation.setSelectedItemId(R.id.menuItem_jots);
            rootPage(new JotListFragment());
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
