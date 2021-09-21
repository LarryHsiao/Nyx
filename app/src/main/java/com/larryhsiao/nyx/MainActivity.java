package com.larryhsiao.nyx;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.fragment.NavHostFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.lang.ref.WeakReference;
import java.util.Objects;

/**
 * Main entry of Activity.
 */
public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final NavHostFragment navFragment = ((NavHostFragment) Objects.requireNonNull(
            getSupportFragmentManager().findFragmentById(R.id.main_fragmentContainer))
        );
        final NavController navController = navFragment.getNavController();
        final BottomNavigationView btmNavView = findViewById(R.id.main_bottomNavigationView);
        btmNavView.setOnItemSelectedListener(item -> {
            for (Fragment fragment : navFragment.getChildFragmentManager().getFragments()) {
                if (fragment instanceof NyxFragment) {
                    final boolean canChange = ((NyxFragment) fragment).preferChangePage(() ->
                        navController.navigate(item.getItemId())
                    );
                    if (!canChange) {
                        return false;
                    }
                }
            }
            navController.navigate(item.getItemId());
            return true;
        });
        final WeakReference<BottomNavigationView> weakRef = new WeakReference<>(btmNavView);
        navController.addOnDestinationChangedListener(
            new NavController.OnDestinationChangedListener() {
                @Override
                public void onDestinationChanged(
                    @NonNull NavController controller,
                    @NonNull NavDestination dest,
                    @Nullable Bundle args
                ) {
                    final BottomNavigationView view = weakRef.get();
                    if (view == null) {
                        navController.removeOnDestinationChangedListener(this);
                        return;
                    }
                    final Menu menu = view.getMenu();
                    for (int h = 0, size = menu.size(); h < size; h++) {
                        MenuItem item = menu.getItem(h);
                        if (matchDestination(dest, item.getItemId())) {
                            item.setChecked(true);
                        }
                    }
                }
            });
    }

    private boolean matchDestination(@NonNull NavDestination dest, @IdRes int destId) {
        NavDestination currentDest = dest;
        while (currentDest.getId() != destId && currentDest.getParent() != null) {
            currentDest = currentDest.getParent();
        }
        return currentDest.getId() == destId;
    }
}
