package com.larryhsiao.nyx;

import android.os.Bundle;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.drawerlayout.widget.DrawerLayout;
import com.google.android.material.navigation.NavigationView;
import com.larryhsiao.nyx.base.JotActivity;
import com.larryhsiao.nyx.jot.JotListFragment;
import com.larryhsiao.nyx.settings.SettingFragment;
import com.larryhsiao.nyx.sync.SyncService;

import static android.view.Gravity.LEFT;
import static com.larryhsiao.nyx.BuildConfig.VERSION_NAME;

/**
 * Entry Activity of Nyx.
 */
public class MainActivity extends JotActivity {
    private int currentPage = -1;
    private DrawerLayout drawer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.page_main);
        setupPageControl(R.id.main_page_container);
        setupFabControl(findViewById(R.id.main_fab));
        ActionBar actionbar = getSupportActionBar();
        if (actionbar != null) {
            actionbar.setHomeButtonEnabled(true);
            actionbar.setDisplayHomeAsUpEnabled(true);
            actionbar.setDisplayShowHomeEnabled(true);
        }

        drawer = findViewById(R.id.main_drawer);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
            this,
            drawer,
            R.string.Open,
            R.string.Close
        );
        drawer.addDrawerListener(toggle);
        getSupportFragmentManager().addOnBackStackChangedListener(() ->
            toggle.setDrawerIndicatorEnabled(
                getSupportFragmentManager().getBackStackEntryCount() == 0
            )
        );
        toggle.syncState();
        NavigationView navigationView = findViewById(R.id.main_navigation);
        navigationView.getMenu().findItem(R.id.menuItem_version)
            .setTitle(getString(R.string.v__, VERSION_NAME));
        navigationView.setNavigationItemSelectedListener(item -> {
                if (item.getItemId() == currentPage) {
                    return false;
                }
                if (item.getItemId() == R.id.menuItem_jots) {
                    currentPage = R.id.menuItem_jots;
                    rootPage(new JotListFragment());
                } else if (item.getItemId() == R.id.menuItem_settings) {
                    currentPage = R.id.menuItem_settings;
                    rootPage(new SettingFragment());
                } else {
                    return true;
                }
                navigationView.getMenu()
                    .findItem(currentPage)
                    .setChecked(true);
                drawer.closeDrawer(LEFT);
                return true;
            }
        );

        if (savedInstanceState == null) {
            navigationView.getMenu()
                .findItem(R.id.menuItem_jots)
                .setChecked(true);
            currentPage = R.id.menuItem_jots;
            rootPage(new JotListFragment());
            SyncService.enqueue(this);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
                if (drawer.isDrawerOpen(LEFT)) {
                    drawer.closeDrawer(LEFT);
                } else {
                    drawer.openDrawer(LEFT);
                }
            } else {
                onBackPressed();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(LEFT)) {
            drawer.closeDrawer(LEFT);
        } else {
            super.onBackPressed();
        }
    }
}
