package com.larryhsiao.nyx.android.jot;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.*;
import android.widget.EditText;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.larryhsiao.nyx.BuildConfig;
import com.larryhsiao.nyx.R;
import com.larryhsiao.nyx.android.base.JotFragment;
import com.larryhsiao.nyx.jots.Jot;
import com.larryhsiao.nyx.jots.JotUri;
import com.larryhsiao.nyx.jots.NewJot;
import com.schibstedspain.leku.LocationPickerActivity;

import static android.app.Activity.RESULT_OK;
import static com.schibstedspain.leku.LocationPickerActivityKt.*;
import static java.lang.Double.MIN_VALUE;

/**
 * Fragment to create new Jot.
 * <p>
 * Use this fragment with
 */
public class NewJotFragment extends JotFragment {
    private static final int REQUEST_CODE_LOCATION_PICKER = 1000;
    private TextView locationText;
    private double[] currentLocation = null;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        setTitle(getString(R.string.new_jot));
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.page_jot, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        locationText = view.findViewById(R.id.jot_location);
        locationText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(
                    new LocationPickerActivity.Builder()
                        .build(v.getContext()),
                    REQUEST_CODE_LOCATION_PICKER
                );
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.jot_edit, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menuItem_save) {
            EditText content = getView().findViewById(R.id.jot_content);
            Jot newJot = new NewJot(
                db, content.getText().toString(), currentLocation
            ).value();
            final Intent intent = new Intent();
            intent.setData(Uri.parse(new JotUri(BuildConfig.URI_HOST, newJot).value().toASCIIString()));
            sendResult(0, RESULT_OK, intent);
            return true;
        }
        return false;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_LOCATION_PICKER && resultCode == RESULT_OK) {
            currentLocation = new double[]{
                data.getDoubleExtra(LONGITUDE, MIN_VALUE),
                data.getDoubleExtra(LATITUDE, MIN_VALUE)
            };
            locationText.setText(data.getStringExtra(LOCATION_ADDRESS));
        }
    }
}

