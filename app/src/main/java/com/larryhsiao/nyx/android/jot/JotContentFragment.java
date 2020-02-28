package com.larryhsiao.nyx.android.jot;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.location.Address;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.gson.Gson;
import com.larryhsiao.nyx.R;
import com.larryhsiao.nyx.android.LocationString;
import com.larryhsiao.nyx.android.base.JotFragment;
import com.larryhsiao.nyx.attachments.AttachmentsByJotId;
import com.larryhsiao.nyx.attachments.NewAttachments;
import com.larryhsiao.nyx.attachments.QueriedAttachments;
import com.larryhsiao.nyx.attachments.RemovalAttachmentByJotId;
import com.larryhsiao.nyx.jots.ConstJot;
import com.larryhsiao.nyx.jots.Jot;
import com.larryhsiao.nyx.jots.JotUri;
import com.larryhsiao.nyx.jots.PostedJot;
import com.larryhsiao.nyx.jots.WrappedJot;
import com.larryhsiao.nyx.tags.JotTagRemoval;
import com.larryhsiao.nyx.tags.NewJotTag;
import com.larryhsiao.nyx.tags.NewTag;
import com.larryhsiao.nyx.tags.QueriedTags;
import com.larryhsiao.nyx.tags.Tag;
import com.larryhsiao.nyx.tags.TagsByJotId;
import com.schibstedspain.leku.LocationPickerActivity;
import com.silverhetch.aura.location.LocationAddress;
import com.silverhetch.clotho.source.ConstSource;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.stream.Collectors;

import static android.app.Activity.RESULT_OK;
import static android.content.Intent.ACTION_OPEN_DOCUMENT;
import static android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION;
import static com.schibstedspain.leku.LocationPickerActivityKt.ADDRESS;
import static com.schibstedspain.leku.LocationPickerActivityKt.LATITUDE;
import static com.schibstedspain.leku.LocationPickerActivityKt.LONGITUDE;
import static java.lang.Double.MIN_VALUE;

/**
 * Fragment that shows the Jot content.
 */
public class JotContentFragment extends JotFragment {
    private static final int REQUEST_CODE_LOCATION_PICKER = 1000;
    private static final int REQUEST_CODE_PICK_FILE = 1001;
    private static final String ARG_JOT_JSON = "ARG_JOT";
    private ChipGroup chipGroup;
    private TextView locationText;
    private AttachmentAdapter attachmentAdapter;
    private Jot jot;

    public static Fragment newInstance(ConstJot jot) {
        final Fragment frag = new JotContentFragment();
        Bundle args = new Bundle();
        args.putString(ARG_JOT_JSON, new Gson().toJson(jot));
        frag.setArguments(args);
        return frag;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (getArguments() != null) {
            jot = new Gson().fromJson(
                getArguments().getString(ARG_JOT_JSON, "{}"),
                ConstJot.class
            );
        } else {
            jot = new ConstJot(
                -1,
                "",
                System.currentTimeMillis(),
                new double[]{MIN_VALUE, MIN_VALUE});
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.page_jot, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TextView date = view.findViewById(R.id.jot_date);
        date.setOnClickListener(v -> {
            DatePickerDialog dialog = new DatePickerDialog(view.getContext());
            dialog.setOnDateSetListener((view1, year, month, dayOfMonth) -> {
                jot = new WrappedJot(jot) {
                    @Override
                    public long createdTime() {
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(year, month, dayOfMonth);
                        return calendar.getTimeInMillis();
                    }
                };
                updateDateIndicator(date);
            });
            dialog.show();
        });
        updateDateIndicator(date);
        chipGroup = view.findViewById(R.id.jot_tagGroup);
        EditText contentEditText = view.findViewById(R.id.jot_content);
        contentEditText.setText(jot.content());
        contentEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Leave it empty
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Leave it empty
            }

            @Override
            public void afterTextChanged(Editable s) {
                jot = new WrappedJot(jot) {
                    @Override
                    public String content() {
                        return s.toString();
                    }
                };
            }
        });
        ImageView attachmentIcon = view.findViewById(R.id.jot_attachment_icon);
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
        locationText.setText(new LocationString(
            new LocationAddress(view.getContext(), location).value()
        ).value());
        final RecyclerView attachmentList = view.findViewById(R.id.jot_attachment_list);
        attachmentList.setAdapter(attachmentAdapter = new AttachmentAdapter());
        attachmentAdapter.loadAttachments(
            new QueriedAttachments(new AttachmentsByJotId(db, jot.id()))
                .value()
                .stream()
                .map(it -> Uri.parse(it.uri()))
                .collect(Collectors.toList())
        );

        ImageView tagIcon = view.findViewById(R.id.jot_tagIcon);
        tagIcon.setOnClickListener(v -> {
            final EditText editText = new EditText(v.getContext());
            editText.setLines(1);
            editText.setMaxLines(1);
            editText.setInputType(InputType.TYPE_CLASS_TEXT);
            new AlertDialog.Builder(v.getContext())
                .setTitle(getString(R.string.new_tag))
                .setMessage(getString(R.string.enter_tag_name))
                .setView(editText)
                .setPositiveButton(R.string.confirm, (dialog, which) -> {
                    final Tag tag = new NewTag(db, editText.getText().toString()).value();
                    final Chip tagChip = new Chip(v.getContext());
                    tagChip.setText(tag.title());
                    tagChip.setLines(1);
                    tagChip.setMaxLines(1);
                    tagChip.setTag(tag);
                    tagChip.setOnClickListener(v1 -> new AlertDialog.Builder(v1.getContext())
                        .setTitle(tagChip.getText().toString())
                        .setMessage(getString(R.string.delete))
                        .setPositiveButton(R.string.confirm, (dialog1, which1) -> chipGroup.removeView(v1))
                        .setNegativeButton(R.string.cancel, null)
                        .show());
                    chipGroup.addView(tagChip);
                })
                .setNegativeButton(R.string.cancel, null)
                .create().show();
        });

        for (Tag tag : new QueriedTags(new TagsByJotId(db, jot.id())).value()) {
            Chip chip = new Chip(view.getContext());
            chip.setTag(tag);
            chip.setText(tag.title());
            chip.setOnClickListener(v -> {
                new AlertDialog.Builder(v.getContext())
                    .setTitle(tag.title())
                    .setMessage(R.string.delete)
                    .setPositiveButton(R.string.confirm, (dialog, which) -> {
                        chipGroup.removeView(v);
                    })
                    .setNegativeButton(R.string.cancel, null)
                    .show();
            });
            chipGroup.addView(chip);
        }
    }

    private void updateDateIndicator(TextView date) {
        date.setText(SimpleDateFormat.getDateInstance().format(new Date(jot.createdTime())));
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.jot_edit, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menuItem_save) {
            jot = new PostedJot(db, jot).value();
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
            new JotTagRemoval(db, jot.id()).fire();
            for (int i = 0; i < chipGroup.getChildCount(); i++) {
                new NewJotTag(
                    db,
                    new ConstSource<>(jot.id()),
                    new ConstSource<>(((Tag) chipGroup.getChildAt(i).getTag()).id())
                ).fire();
            }
            final Intent intent = new Intent();
            intent.setData(Uri.parse(new JotUri(jot).value().toASCIIString()));
            sendResult(0, RESULT_OK, intent);
            return true;
        }
        return false;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_LOCATION_PICKER && resultCode == RESULT_OK) {
            jot = new WrappedJot(jot) {
                @Override
                public double[] location() {
                    return new double[]{
                        data.getDoubleExtra(LONGITUDE, 0.0),
                        data.getDoubleExtra(LATITUDE, 0.0)
                    };
                }
            };
            Address address = data.getParcelableExtra(ADDRESS);
            locationText.setText(new LocationString(address).value());
        } else if (requestCode == REQUEST_CODE_PICK_FILE && resultCode == RESULT_OK) {
            attachmentAdapter.appendImage(data.getData());
            getContext().getContentResolver().takePersistableUriPermission(
                data.getData(),
                FLAG_GRANT_READ_URI_PERMISSION
            );
        }
    }
}
