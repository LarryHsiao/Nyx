package com.larryhsiao.nyx.util;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.larryhsiao.nyx.R;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

/**
 * Fragment for display a place by google map.
 */
public class EmbedMapFragment extends Fragment {
    private static final String KEY_LOCATION = "KEY_LOCATION";
    private double[] location;
    private int viewId;

    public static Fragment newInstance(double longitude, double latitude) {
        Fragment fragment = new EmbedMapFragment();
        Bundle bundle = new Bundle();
        bundle.putDoubleArray(KEY_LOCATION, new double[]{longitude, latitude});
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        location = getArguments().getDoubleArray(KEY_LOCATION);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        final FrameLayout root = new FrameLayout(inflater.getContext());
        root.setLayoutParams(new ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT));
        root.setId(viewId = View.generateViewId());
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        SupportMapFragment mapFrag = SupportMapFragment.newInstance();
        mapFrag.getMapAsync(googleMap -> {
            googleMap.setMyLocationEnabled(false);
            UiSettings settings = googleMap.getUiSettings();
            settings.setAllGesturesEnabled(false);
            settings.setMapToolbarEnabled(false);
            CameraUpdate update = CameraUpdateFactory.newLatLngZoom(new LatLng(
                location[1], location[0]
            ), 15);
            googleMap.moveCamera(update);
            MarkerOptions marker = new MarkerOptions();
            marker.position(new LatLng(location[1],location[0]));
            googleMap.addMarker(marker);
        });
        getChildFragmentManager().beginTransaction()
            .replace(viewId, mapFrag)
            .commit();
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
}
