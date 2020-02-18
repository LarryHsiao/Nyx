package com.larryhsiao.nyx.android.jot;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.*;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.larryhsiao.nyx.BuildConfig;
import com.larryhsiao.nyx.R;
import com.larryhsiao.nyx.android.base.JotFragment;
import com.larryhsiao.nyx.attachments.NewAttachments;
import com.larryhsiao.nyx.jots.Jot;
import com.larryhsiao.nyx.jots.JotUri;
import com.larryhsiao.nyx.jots.NewJot;
import com.larryhsiao.nyx.tags.NewJotTag;
import com.larryhsiao.nyx.tags.NewTag;
import com.larryhsiao.nyx.tags.Tag;
import com.schibstedspain.leku.LocationPickerActivity;
import com.silverhetch.clotho.source.ConstSource;

import java.util.stream.Collectors;

import static android.app.Activity.RESULT_OK;
import static android.content.Intent.ACTION_OPEN_DOCUMENT;
import static com.schibstedspain.leku.LocationPickerActivityKt.*;
import static java.lang.Double.MIN_VALUE;

/**
 * Fragment to create new Jot.
 * <p>
 * Use this fragment with
 */
public class NewJotFragment extends JotFragment {
    private static final int REQUEST_CODE_LOCATION_PICKER = 1000;
    private static final int REQUEST_CODE_FILE_PICKER = 1001;
    private ChipGroup tagGroup;
    private TextView locationText;
    private ImageView attachmentIcon;
    private AttachmentAdapter attachmentAdapter;
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
        tagGroup = view.findViewById(R.id.jot_tagGroup);
        attachmentIcon = view.findViewById(R.id.jot_attachment_icon);
        attachmentIcon.setOnClickListener(v -> {
            final Intent intent = new Intent(ACTION_OPEN_DOCUMENT);
            intent.setType("image/*");
            startActivityForResult(intent, REQUEST_CODE_FILE_PICKER);
        });
        locationText = view.findViewById(R.id.jot_location);
        locationText.setOnClickListener(v -> startActivityForResult(
            new LocationPickerActivity.Builder()
                .build(v.getContext()),
            REQUEST_CODE_LOCATION_PICKER
        ));
        final RecyclerView attachmentList = view.findViewById(R.id.jot_attachment_list);
        attachmentList.setAdapter(attachmentAdapter = new AttachmentAdapter());

        ImageView tagIcon = view.findViewById(R.id.jot_tagIcon);
        tagIcon.setOnClickListener(v -> {
            final EditText editText = new EditText(v.getContext());
            new AlertDialog.Builder(v.getContext())
                .setTitle(getString(R.string.new_tag))
                .setMessage(getString(R.string.enter_tag_name))
                .setView(editText)
                .setPositiveButton(R.string.confirm, (dialog, which) -> {
                    Tag tag = new NewTag(
                        db, editText.getText().toString()
                    ).value();
                    Chip tagChip = new Chip(v.getContext());
                    tagChip.setText(tag.title());
                    tagChip.setLines(1);
                    tagChip.setMaxLines(1);
                    tagChip.setTag(tag);
                    tagChip.setOnClickListener(v1 -> new AlertDialog.Builder(v1.getContext())
                        .setTitle(tagChip.getText().toString())
                        .setMessage(getString(R.string.delete))
                        .setPositiveButton(R.string.confirm, (dialog1, which1) -> tagGroup.removeView(v1))
                        .setNegativeButton(R.string.cancel, null)
                        .show());
                    tagGroup.addView(tagChip);
                })
                .setNegativeButton(R.string.cancel, null)
                .create().show();
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
            new NewAttachments(
                db,
                newJot.id(),
                attachmentAdapter.exportUri()
                    .stream()
                    .map(v -> v.toString())
                    .collect(Collectors.toList())
                    .toArray(new String[0])
            ).value();
            for (int i = 0; i < tagGroup.getChildCount(); i++) {
                new NewJotTag(db,
                    new ConstSource<>(newJot.id()),
                    new ConstSource<>(((Tag) tagGroup.getChildAt(i).getTag()).id())
                ).fire();
            }
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
        } else if (requestCode == REQUEST_CODE_FILE_PICKER && resultCode == RESULT_OK) {
            attachmentAdapter.appendImage(data.getData());
            getContext().getContentResolver().takePersistableUriPermission(data.getData(), Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }
    }
}

