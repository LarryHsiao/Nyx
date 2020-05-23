package com.larryhsiao.nyx.jot;

import android.app.AlertDialog;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.larryhsiao.nyx.R;
import com.larryhsiao.nyx.core.jots.ConstJot;
import com.larryhsiao.nyx.core.jots.Jot;
import com.larryhsiao.nyx.core.jots.JotById;
import com.larryhsiao.nyx.core.jots.JotUriId;
import com.larryhsiao.nyx.core.jots.JotsByCheckedFilter;
import com.larryhsiao.nyx.core.jots.QueriedJots;
import com.larryhsiao.nyx.core.jots.filter.Filter;
import com.silverhetch.aura.view.fab.FabBehavior;
import com.silverhetch.clotho.Source;

import java.sql.ResultSet;
import java.util.List;
import java.util.stream.Collectors;

import static android.app.Activity.RESULT_OK;
import static java.lang.Double.MIN_VALUE;

/**
 * Fragment that shows jots by map.
 *
 * @todo #0 Preview image on a marker.
 */
public class JotMapFragment extends JotListingFragment {
    private static final int REQUEST_CODE_NEW_JOT = 1000;
    private static final int REQUEST_CODE_UPDATE_JOT = 1001;
    private ClusterManager<JotMapItem> clusterManger;
    private GoogleMap map;
    private CameraPosition cameraPos;
    private Marker selectedMarker = null;

    public static Fragment newInstance(JotListingFragment listingFrag) {
        JotMapFragment frag = new JotMapFragment();
        Bundle bundle = new Bundle();
        listingFrag.setupFilterArgs(bundle);
        frag.setArguments(bundle);
        return frag;
    }

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
        attachFab(new FabBehavior() {
            @Override
            public int icon() {
                return R.drawable.ic_plus;
            }

            @Override
            public void onClick() {
                Fragment frag = JotContentFragment.newInstance();
                frag.setTargetFragment(JotMapFragment.this, REQUEST_CODE_NEW_JOT);
                nextPage(frag);
            }
        });
        SupportMapFragment mapFrag = SupportMapFragment.newInstance();
        mapFrag.getMapAsync(googleMap -> {
            map = googleMap;
            setupMap();
            loadJots();
        });
        getChildFragmentManager().beginTransaction()
            .replace(R.id.map_container, mapFrag)
            .commit();
    }

    private void setupMap() {
        clusterManger = new ClusterManager<>(requireContext(), map);
        map.setOnCameraIdleListener(clusterManger);
        map.setOnMarkerClickListener(clusterManger);
        map.setOnInfoWindowClickListener(clusterManger);
        clusterManger.setRenderer(new DefaultClusterRenderer<>(
            requireContext(),
            map,
            clusterManger
        ));
        clusterManger.setOnClusterClickListener(cluster -> {
            nextPage(JotListFragment.newInstanceByJotIds(
                cluster.getPosition().latitude + ", " + cluster.getPosition().longitude + "",
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
            final MarkerOptions option = new MarkerOptions();
            option.position(latLng);
            option.title(latLng.latitude + ", " + latLng.longitude + "");
            selectedMarker = map.addMarker(option);
            selectedMarker.showInfoWindow();
        });

        map.setOnInfoWindowLongClickListener(marker -> {
            final Location location = new Location("Address");
            location.setLongitude(marker.getPosition().longitude);
            location.setLatitude(marker.getPosition().latitude);
            ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1);
            adapter.add(getString(R.string.new_jot));
            new AlertDialog.Builder(requireContext())
                .setTitle(
                    marker.getPosition().latitude + ", " + marker.getPosition().longitude
                ).setAdapter(adapter, (dialog, which) -> {
                if (which == 0) {
                    Fragment frag = JotContentFragment.newInstance(
                        new ConstJot(
                            -1,
                            "",
                            System.currentTimeMillis(),
                            new double[]{
                                marker.getPosition().longitude,
                                marker.getPosition().latitude
                            },
                            "",
                            1,
                            false));
                    frag.setTargetFragment(JotMapFragment.this, REQUEST_CODE_NEW_JOT);
                    nextPage(frag);
                }
            }).show();
        });
    }

    @Override
    protected void loadJots(Filter filter) {
        if (map != null) {
            loadData(new JotsByCheckedFilter(db, filter));
        }
    }

    private void loadData(Source<ResultSet> query) {
        final List<Jot> jots = new QueriedJots(query).value()
            .stream()
            .filter(it -> it.location()[0] != MIN_VALUE && it.location()[1] != MIN_VALUE &&
                it.location()[0] != 0.0 && it.location()[1] != 0.0)
            .collect(Collectors.toList());
        clusterManger.clearItems();
        clusterManger.setRenderer(new DefaultClusterRenderer<>(
            requireContext(),
            map,
            clusterManger
        ));

        if (jots.size() > 0) {
            LatLngBounds.Builder bounds = LatLngBounds.builder();
            for (Jot jot : jots) {
                clusterManger.addItem(new JotMapItem(jot));
                bounds.include(new LatLng(jot.location()[1], jot.location()[0]));
            }
            if (cameraPos == null) {
                map.moveCamera(
                    CameraUpdateFactory.newLatLngBounds(
                        bounds.build(),
                        200
                    )
                );
            } else {
                map.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPos));
                cameraPos = null;
            }
        } else {
            if (cameraPos != null) {
                map.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPos));
                cameraPos = null;
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        detachFab();
        final Fragment mapFrag = getChildFragmentManager().findFragmentById(R.id.map_container);
        if (mapFrag != null) {
            cameraPos = map.getCameraPosition();
            map = null;
            getChildFragmentManager().beginTransaction()
                .remove(mapFrag)
                .commit();
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.findItem(R.id.menuItem_viewMode).setIcon(R.drawable.ic_agenda);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menuItem_viewMode) {
            rootPage(JotListFragment.newInstance(this));
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_NEW_JOT && resultCode == RESULT_OK && data != null) {
            clusterManger.addItem(new JotMapItem(
                new JotById(
                    new JotUriId(
                        data.getData().toString()).value(), db
                ).value()
            ));
            getParentFragmentManager().popBackStack();
        }

        if (requestCode == REQUEST_CODE_UPDATE_JOT && resultCode == RESULT_OK && data != null) {
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
            getParentFragmentManager().popBackStack();
        }
    }
}
