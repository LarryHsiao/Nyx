package com.larryhsiao.nyx.android.jot;

import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.view.*;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import com.larryhsiao.nyx.R;
import com.larryhsiao.nyx.android.base.JotFragment;
import com.larryhsiao.nyx.attachments.AttachmentsByJotId;
import com.larryhsiao.nyx.attachments.NewAttachments;
import com.larryhsiao.nyx.attachments.QueriedAttachments;
import com.larryhsiao.nyx.attachments.RemovalAttachmentByJotId;
import com.larryhsiao.nyx.jots.*;
import com.schibstedspain.leku.LocationPickerActivity;
import com.silverhetch.aura.location.LocationAddress;
import com.silverhetch.clotho.source.ConstSource;
import com.squareup.picasso.Picasso;
import com.stfalcon.imageviewer.StfalconImageViewer;

import java.util.Collections;
import java.util.stream.Collectors;

import static android.app.Activity.RESULT_OK;
import static android.content.Intent.ACTION_OPEN_DOCUMENT;
import static android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION;
import static com.schibstedspain.leku.LocationPickerActivityKt.*;

/**
 * Fragment that shows the Jot content.
 */
public class JotContentFragment extends JotFragment {
    private static final int REQUEST_CODE_LOCATION_PICKER = 1000;
    private static final int REQUEST_CODE_PICK_FILE = 1001;
    private static final String ARG_JOT_ID = "ARG_JOT_ID";
    private EditText contentEditText;
    private TextView locationText;
    private ImageView attachmentIcon;
    private AttachmentAdapter attachmentAdapter;
    private double[] currentLocation = null;
    private Jot jot;

    public static Fragment newInstance(long jotId) {
        final Fragment frag = new JotContentFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_JOT_ID, jotId);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        jot = new JotById(getArguments().getLong(ARG_JOT_ID), db).value();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.page_jot, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        contentEditText = view.findViewById(R.id.jot_content);
        contentEditText.setText(jot.content());
        attachmentIcon = view.findViewById(R.id.jot_attachment_icon);
        attachmentIcon.setOnClickListener(v -> {
            final Intent intent = new Intent(ACTION_OPEN_DOCUMENT);
            intent.setType("image/*");
            startActivityForResult(intent, REQUEST_CODE_PICK_FILE);
        });
        locationText = view.findViewById(R.id.jot_location);
        locationText.setOnClickListener(v -> startActivityForResult(
            new LocationPickerActivity.Builder()
                .build(view.getContext()),
            REQUEST_CODE_LOCATION_PICKER
        ));
        Location location = new Location("Constant");
        location.setLongitude(jot.location()[0]);
        location.setLatitude(jot.location()[1]);
        locationText.setText(new LocationAddress(view.getContext(), location).value().getAddressLine(0));
        currentLocation = new double[]{
            jot.location()[0],
            jot.location()[1]
        };
        final RecyclerView attachmentList = view.findViewById(R.id.jot_attachment_list);
        attachmentList.setAdapter(attachmentAdapter = new AttachmentAdapter(uri -> {
            new StfalconImageViewer.Builder<>(
                attachmentList.getContext(),
                Collections.singletonList(uri),
                (imageView, image) -> Picasso.get().load(image).into(imageView)).show();
            return null;
        }));
        attachmentAdapter.loadAttachments(
            new QueriedAttachments(new AttachmentsByJotId(db, jot.id()))
                .value()
                .stream()
                .map(it -> Uri.parse(it.uri()))
                .collect(Collectors.toList())
        );
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.jot_edit, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menuItem_save) {
            new UpdateJot(new UpdatedJot(
                jot,
                contentEditText.getText().toString(),
                new ConstSource<>(currentLocation)
            ), db).fire();
            new RemovalAttachmentByJotId(db, jot.id()).fire();
            new NewAttachments(
                db,
                jot.id(),
                attachmentAdapter.exportUri()
                    .stream()
                    .map(it -> it.toString())
                    .collect(Collectors.toList())
                    .toArray(new String[0])
            ).value();
            final Intent intent = new Intent();
            intent.setData(Uri.parse(new JotUri(
                jot
            ).value().toASCIIString()));
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
                data.getDoubleExtra(LONGITUDE, 0.0),
                data.getDoubleExtra(LATITUDE, 0.0)
            };
            locationText.setText(data.getStringExtra(LOCATION_ADDRESS));
        } else if (requestCode == REQUEST_CODE_PICK_FILE && resultCode == RESULT_OK) {
            attachmentAdapter.appendImage(data.getData());
            getContext().getContentResolver().takePersistableUriPermission(
                data.getData(),
                FLAG_GRANT_READ_URI_PERMISSION
            );
        }
    }
}
