package com.larryhsiao.nyx.jot;

import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.SearchView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.larryhsiao.nyx.LocationString;
import com.larryhsiao.nyx.R;
import com.larryhsiao.nyx.base.JotFragment;
import com.larryhsiao.nyx.core.jots.AllJots;
import com.larryhsiao.nyx.core.jots.ConstJot;
import com.larryhsiao.nyx.core.jots.Jot;
import com.larryhsiao.nyx.core.jots.JotById;
import com.larryhsiao.nyx.core.jots.JotUriId;
import com.larryhsiao.nyx.core.jots.JotsByKeyword;
import com.larryhsiao.nyx.core.jots.QueriedJots;
import com.silverhetch.aura.location.LocationAddress;
import com.silverhetch.clotho.Source;

import java.sql.ResultSet;
import java.util.List;
import java.util.stream.Collectors;

import static android.app.Activity.RESULT_OK;
import static java.lang.Double.MIN_VALUE;

/**
 * Fragment that shows jots by map.
 */
public class JotMapFragment extends JotFragment {
    private static final int REQUEST_CODE_NEW_JOT = 1000;
    private static final int REQUEST_CODE_UPDATE_JOT = 1001;
    private ClusterManager<JotMapItem> clusterManger;
    private GoogleMap map;
    private Marker selectedMarker = null;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        setTitle(getString(R.string.map));
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(
            R.layout.page_map,
            container,
            false
        );
    }

    @Override
    public void onResume() {
        super.onResume();
        SupportMapFragment mapFrag = SupportMapFragment.newInstance();
        mapFrag.getMapAsync(googleMap -> {
            map = googleMap;
            setupMap();
            loadData(new AllJots(db));
        });
        getChildFragmentManager().beginTransaction()
            .replace(R.id.map_container, mapFrag)
            .commit();
    }

    private void setupMap() {
        clusterManger = new ClusterManager<>(getContext(), map);
        map.setOnCameraIdleListener(clusterManger);
        map.setOnMarkerClickListener(clusterManger);
        map.setOnInfoWindowClickListener(clusterManger);
        clusterManger.setRenderer(new DefaultClusterRenderer<>(
            getContext(),
            map,
            clusterManger
        ));
        clusterManger.setOnClusterClickListener(cluster -> {
            nextPage(JotListFragment.newInstanceByJotIds(
                cluster.getItems().stream()
                    .mapToLong(it -> it.getJot().id())
                    .toArray()
            ));
            return true;
        });
        clusterManger.setOnClusterItemClickListener(cluster -> {
            Fragment frag = JotContentFragment.newInstance(new ConstJot(cluster.getJot()));
            frag.setTargetFragment(this, REQUEST_CODE_UPDATE_JOT);
            nextPage(frag);
            return true;
        });
        clusterManger.setOnClusterItemInfoWindowClickListener(jotMapItem -> {
            Fragment frag = JotContentFragment.newInstance(new ConstJot(jotMapItem.getJot()));
            frag.setTargetFragment(this, REQUEST_CODE_UPDATE_JOT);
            nextPage(frag);
        });
        map.setOnMapClickListener(latLng -> {
            if (selectedMarker != null) {
                selectedMarker.remove();
            }
            final Location location = new Location("Const");
            location.setLatitude(latLng.latitude);
            location.setLongitude(latLng.longitude);
            final Address address = new LocationAddress(getContext(), location).value();
            final MarkerOptions option = new MarkerOptions();
            option.position(latLng);
            option.title(new LocationString(address).value());
            selectedMarker = map.addMarker(option);
            selectedMarker.showInfoWindow();
        });

        map.setOnInfoWindowLongClickListener(marker -> {
            final Location location = new Location("Address");
            location.setLongitude(marker.getPosition().longitude);
            location.setLatitude(marker.getPosition().latitude);
            ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1);
            adapter.add(getString(R.string.new_jot));
            new AlertDialog.Builder(getContext())
                .setTitle(new LocationString(new LocationAddress(getContext(), location).value()).value())
                .setAdapter(adapter, (dialog, which) -> {
                    if (which == 0) {
                        Fragment frag = JotContentFragment.newInstance(new ConstJot(
                            -1,
                            "",
                            System.currentTimeMillis(),
                            new double[]{
                                marker.getPosition().longitude,
                                marker.getPosition().latitude
                            },
                            ""
                        ));
                        frag.setTargetFragment(JotMapFragment.this, REQUEST_CODE_NEW_JOT);
                        nextPage(frag);
                    }
                })
                .show();
        });
    }

    private void loadData(Source<ResultSet> query) {
        final List<Jot> jots = new QueriedJots(query).value()
            .stream()
            .filter(it -> it.location()[0] != MIN_VALUE && it.location()[1] != MIN_VALUE &&
                it.location()[0] != 0.0 && it.location()[1] != 0.0)
            .collect(Collectors.toList());
        clusterManger.clearItems();
        clusterManger.setRenderer(new DefaultClusterRenderer<>(
            getContext(),
            map,
            clusterManger
        ));
        if (jots.size() > 0) {
            LatLngBounds.Builder bounds = LatLngBounds.builder();
            for (Jot jot : jots) {
                clusterManger.addItem(new JotMapItem(jot));
                bounds.include(new LatLng(jot.location()[1], jot.location()[0]));
            }
            map.moveCamera(
                CameraUpdateFactory.newLatLngBounds(
                    bounds.build(),
                    200
                )
            );
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        final Fragment mapFrag = getChildFragmentManager().findFragmentById(R.id.map_container);
        if (mapFrag != null) {
            getChildFragmentManager().beginTransaction()
                .remove(mapFrag)
                .commit();
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.jot_list, menu);

        SearchManager searchManager = ((SearchManager) getContext().getSystemService(Context.SEARCH_SERVICE));
        MenuItem searchMenuItem = menu.findItem(R.id.menuItem_search);
        SearchView searchView = (SearchView) searchMenuItem.getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
        searchView.setOnCloseListener(() -> {
            searchMenuItem.collapseActionView();
            loadData(new AllJots(db));
            return false;
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (map != null) {
                    loadData(new JotsByKeyword(db, newText));
                } else {
                    searchView.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            onQueryTextChange(newText);
                        }
                    }, 100);
                }
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
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menuItem_newJot) {
            Fragment frag = new JotContentFragment();
            frag.setTargetFragment(this, REQUEST_CODE_NEW_JOT);
            nextPage(frag);
            return true;
        }
        return false;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_NEW_JOT && resultCode == RESULT_OK) {
            clusterManger.addItem(new JotMapItem(
                new JotById(
                    new JotUriId(
                        data.getData().toString()).value(), db
                ).value()
            ));
            getFragmentManager().popBackStack();
        }

        if (requestCode == REQUEST_CODE_UPDATE_JOT && resultCode == RESULT_OK) {
            long updatedId = new JotUriId(data.getData().toString()).value();
            JotMapItem updateItem = null;
            for (JotMapItem item : clusterManger.getAlgorithm().getItems()) {
                if (item.getJot().id() == updatedId) {
                    updateItem = item;
                    break;
                }
            }
            clusterManger.removeItem(updateItem);
            clusterManger.addItem(new JotMapItem(
                new JotById(updatedId, db).value()
            ));
            getFragmentManager().popBackStack();
        }
    }
}
