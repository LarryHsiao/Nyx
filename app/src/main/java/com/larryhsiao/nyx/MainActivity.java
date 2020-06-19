package com.larryhsiao.nyx;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;
import com.bumptech.glide.Glide;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.larryhsiao.nyx.base.JotActivity;
import com.larryhsiao.nyx.jot.JotListFragment;
import com.larryhsiao.nyx.settings.SettingFragment;
import com.larryhsiao.nyx.sync.SyncService;
import com.silverhetch.aura.view.span.ClickableStr;
import com.silverhetch.aura.view.span.ColoredStr;
import com.silverhetch.clotho.source.ConstSource;

import java.util.Collections;

import static android.graphics.Color.BLUE;
import static android.text.method.LinkMovementMethod.getInstance;
import static android.view.Gravity.LEFT;
import static android.widget.Toast.LENGTH_SHORT;
import static androidx.swiperefreshlayout.widget.CircularProgressDrawable.LARGE;
import static com.bumptech.glide.request.RequestOptions.circleCropTransform;
import static com.larryhsiao.nyx.BuildConfig.VERSION_NAME;

/**
 * Entry Activity of Nyx.
 */
public class MainActivity extends JotActivity {
    private static final int REQUEST_CODE_LOG_IN = 1000;
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
        navigationView.getMenu()
            .findItem(R.id.menuItem_version)
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
        updateLoginState();
    }

    @Override
    protected void onActivityResult(
        int requestCode,
        int resultCode,
        @org.jetbrains.annotations.Nullable Intent data
    ) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_LOG_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                updateLoginState();
//                requestSub(false);
            } else {
                Toast.makeText(
                    this,
                    "error " + response, LENGTH_SHORT
                ).show();
            }
        }
    }

    private void updateLoginState() {
        NavigationView nav = findViewById(R.id.main_navigation);
        View header = nav.getHeaderView(0);
        ImageView userIcon = header.findViewById(R.id.navHeader_userIcon);
        TextView text = header.findViewById(R.id.navHeader_info);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            userIcon.setImageResource(R.drawable.ic_user);
            text.setText(new ClickableStr(
                new ColoredStr(
                    new ConstSource<>(getString(R.string.Log_in)),
                    BLUE
                ),
                () -> startActivityForResult(
                    AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setIsSmartLockEnabled(false)
                        .setAvailableProviders(Collections.singletonList(
                            new AuthUI.IdpConfig.GoogleBuilder().build()
                        )).build(),
                    REQUEST_CODE_LOG_IN
                )
            ).value());
            text.setMovementMethod(getInstance());
        } else {
            final CircularProgressDrawable placeholder =
                new CircularProgressDrawable(this);
            placeholder.setStyle(LARGE);
            placeholder.start();
            Glide.with(this).load(user.getPhotoUrl())
                .error(getResources().getDrawable(R.drawable.ic_user, null))
                .placeholder(placeholder)
                .apply(circleCropTransform())
                .into(userIcon);
            text.setText(user.getDisplayName());
            text.append("\n");
            text.append(user.getEmail());
            text.setMovementMethod(LinkMovementMethod.getInstance());
            text.append("\n");
            text.append(new ClickableStr(
                new ColoredStr(
                    new ConstSource<>(getString(R.string.logout)),
                    BLUE
                ),
                () -> {
                    AuthUI.getInstance().signOut(this);
                    updateLoginState();
                }
            ).value());
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
