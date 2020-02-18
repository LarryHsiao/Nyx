package com.larryhsiao.nyx.android.jot;

import android.content.Intent;
import android.os.Bundle;
import android.view.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.larryhsiao.nyx.R;
import com.larryhsiao.nyx.android.base.JotFragment;
import com.larryhsiao.nyx.jots.*;

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
            loadData();
        });
        getChildFragmentManager().beginTransaction()
            .replace(R.id.map_container, mapFrag)
            .commit();
    }

    private void loadData() {
        final List<Jot> jots = new QueriedJots(new AllJots(db)).value()
            .stream()
            .filter(it-> it.location()[0]!= MIN_VALUE && it.location()[1]!= MIN_VALUE)
            .collect(Collectors.toList());
        clusterManger = new ClusterManager<>(getContext(), map);
        map.setOnCameraIdleListener(clusterManger);
        map.setOnMarkerClickListener(clusterManger);
        map.setOnInfoWindowClickListener(clusterManger);
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
        final DefaultClusterRenderer<JotMapItem> renderer = new DefaultClusterRenderer<>(
            getContext(),
            map,
            clusterManger
        );
        clusterManger.setRenderer(renderer);

        clusterManger.setOnClusterClickListener(cluster -> {
            nextPage(JotListFragment.newInstanceByJotIds(
                cluster.getItems().stream()
                    .mapToLong(it -> it.getId())
                    .toArray()
            ));
            return true;
        });
        clusterManger.setOnClusterItemClickListener(cluster -> {
            Fragment frag = JotContentFragment.newInstance(cluster.getId());
            frag.setTargetFragment(this, REQUEST_CODE_UPDATE_JOT);
            nextPage(frag);
            return true;
        });
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
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menuItem_newJot) {
            Fragment frag = new NewJotFragment();
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
                if (item.getId() == updatedId) {
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
