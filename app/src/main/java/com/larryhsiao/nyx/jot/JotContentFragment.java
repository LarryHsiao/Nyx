package com.larryhsiao.nyx.jot;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.ClipData;
import android.content.Intent;
import android.location.Address;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.exifinterface.media.ExifInterface;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.gson.Gson;
import com.larryhsiao.nyx.BuildConfig;
import com.larryhsiao.nyx.LocationString;
import com.larryhsiao.nyx.R;
import com.larryhsiao.nyx.attachments.AttachmentsFragment;
import com.larryhsiao.nyx.base.JotFragment;
import com.larryhsiao.nyx.core.attachments.Attachment;
import com.larryhsiao.nyx.core.attachments.AttachmentsByJotId;
import com.larryhsiao.nyx.core.attachments.NewAttachment;
import com.larryhsiao.nyx.core.attachments.QueriedAttachments;
import com.larryhsiao.nyx.core.attachments.RemovalAttachment;
import com.larryhsiao.nyx.core.attachments.UpdateAttachment;
import com.larryhsiao.nyx.core.attachments.WrappedAttachment;
import com.larryhsiao.nyx.core.jots.ConstJot;
import com.larryhsiao.nyx.core.jots.Jot;
import com.larryhsiao.nyx.core.jots.JotRemoval;
import com.larryhsiao.nyx.core.jots.JotUri;
import com.larryhsiao.nyx.core.jots.PostedJot;
import com.larryhsiao.nyx.core.jots.QueriedJots;
import com.larryhsiao.nyx.core.jots.WrappedJot;
import com.larryhsiao.nyx.core.tags.AllTags;
import com.larryhsiao.nyx.core.tags.CreatedTagByName;
import com.larryhsiao.nyx.core.tags.JotTagRemoval;
import com.larryhsiao.nyx.core.tags.JotsByTagId;
import com.larryhsiao.nyx.core.tags.NewJotTag;
import com.larryhsiao.nyx.core.tags.NewTag;
import com.larryhsiao.nyx.core.tags.QueriedTags;
import com.larryhsiao.nyx.core.tags.Tag;
import com.larryhsiao.nyx.core.tags.TagsByJotId;
import com.larryhsiao.nyx.core.tags.TagsByKeyword;
import com.larryhsiao.nyx.sync.SyncService;
import com.larryhsiao.nyx.util.EmbedMapFragment;
import com.schibstedspain.leku.LocationPickerActivity;
import com.silverhetch.aura.BackControl;
import com.silverhetch.aura.location.LocationAddress;
import com.silverhetch.aura.uri.UriMimeType;
import com.silverhetch.aura.view.alert.Alert;
import com.silverhetch.aura.view.dialog.FullScreenDialogFragment;
import com.silverhetch.aura.view.dialog.InputDialog;
import com.silverhetch.aura.view.fab.FabBehavior;
import com.silverhetch.clotho.source.ConstSource;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
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
public class JotContentFragment extends JotFragment implements BackControl {
    private static final int REQUEST_CODE_LOCATION_PICKER = 1000;
    private static final int REQUEST_CODE_PICK_FILE = 1001;
    private static final int REQUEST_CODE_INPUT_CUSTOM_MOOD = 1002;
    private static final int REQUEST_CODE_ALERT = 1003;
    private static final int REQUEST_CODE_ATTACHMENT_DIALOG = 1004;

    private static final String ARG_JOT_JSON = "ARG_JOT";
    private HandlerThread backgroundThread;
    private Handler backgroundHandler;
    private ChipGroup chipGroup;
    private TextView locationText;
    private TextView moodText;
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
    public void onDestroy() {
        super.onDestroy();
        backgroundThread.quitSafely();
        backgroundThread.interrupt();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        backgroundThread = new HandlerThread("ContentBackground");
        backgroundThread.start();
        backgroundHandler = new Handler(backgroundThread.getLooper());
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
                new double[]{MIN_VALUE, MIN_VALUE},
                "",
                1,
                false
            );
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
        view.findViewById(R.id.jot_newAttachment).setOnClickListener(it -> {
            final Intent intent = new Intent(ACTION_OPEN_DOCUMENT);
            intent.setType("image/*");
            intent.putExtra(Intent.EXTRA_MIME_TYPES, new String[]{"image/*", "video/*", "audio/*"});
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            startActivityForResult(intent, REQUEST_CODE_PICK_FILE);
        });
        ImageView attachmentIcon = view.findViewById(R.id.jot_attachment_icon);
        attachmentIcon.setOnClickListener(v -> {
            FullScreenDialogFragment dialog = AttachmentsFragment.newInstance(attachmentAdapter.exportUri());
            dialog.setTargetFragment(this, REQUEST_CODE_ATTACHMENT_DIALOG);
            dialog.show(getParentFragmentManager(), null);
        });
        locationText = view.findViewById(R.id.jot_location);
        locationText.setOnClickListener(v -> pickLocation());
        Location location = new Location("Constant");
        location.setLongitude(jot.location()[0]);
        location.setLatitude(jot.location()[1]);
        updateAddress(location);
        loadEmbedMapByJot();
        final RecyclerView attachmentList = view.findViewById(R.id.jot_attachment_list);
        attachmentList.setAdapter(attachmentAdapter = new AttachmentAdapter(view.getContext()));
        attachmentAdapter.loadAttachments(
            new QueriedAttachments(new AttachmentsByJotId(db, jot.id()))
                .value()
                .stream()
                .map(it -> Uri.parse(it.uri()))
                .collect(Collectors.toList())
        );

        ImageView tagIcon = view.findViewById(R.id.jot_tagIcon);
        tagIcon.setOnClickListener(v -> {
            final AutoCompleteTextView editText = new AutoCompleteTextView(v.getContext());
            editText.setLines(1);
            editText.setMaxLines(1);
            editText.setInputType(InputType.TYPE_CLASS_TEXT);
            editText.setAdapter(new ArrayAdapter<>(
                    v.getContext(),
                    android.R.layout.simple_dropdown_item_1line,
                    new QueriedTags(
                        new AllTags(db)
                    ).value().stream().map(Tag::title).collect(Collectors.toList())
                )
            );
            new AlertDialog.Builder(v.getContext())
                .setTitle(getString(R.string.new_tag))
                .setMessage(getString(R.string.enter_tag_name))
                .setView(editText)
                .setPositiveButton(R.string.confirm, (dialog, which) -> {
                    final String preferTagName = editText.getText().toString();
                    Tag tag = null;
                    for (Tag searched : new QueriedTags(new TagsByKeyword(db, preferTagName)).value()) {
                        if (searched.title().equals(preferTagName)) {
                            tag = searched;
                            break;
                        }
                    }
                    if (tag == null) {
                        tag = new NewTag(db, editText.getText().toString()).value();
                    }
                    final Chip tagChip = new Chip(v.getContext());
                    tagChip.setText(tag.title());
                    tagChip.setLines(1);
                    tagChip.setMaxLines(1);
                    tagChip.setTag(tag);
                    tagChip.setOnClickListener(v1 -> tagChipClicked(tagChip));
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
        moodText = view.findViewById(R.id.jot_mood);

        String mood = String.valueOf(jot.mood());
        if (mood.isEmpty()) {
            mood = "+";
        }
        moodText.setText(mood);
        moodText.setOnClickListener(v -> {
            final GridView gridView = new GridView(v.getContext());
            gridView.setNumColumns(4);
            gridView.setAdapter(new MoodAdapter(v.getContext()));
            AlertDialog moodDialog = new AlertDialog.Builder(v.getContext())
                .setTitle(getString(R.string.moods))
                .setNegativeButton(R.string.cancel, (dialog, which) -> {
                })
                .setView(gridView)
                .show();
            gridView.setOnItemClickListener((parent, view12, position, id) -> {
                if (position == gridView.getAdapter().getCount() - 1) { // last custom dialog
                    moodDialog.dismiss();
                    InputDialog dialog = InputDialog.Companion.newInstance(
                        getString(R.string.moods),
                        REQUEST_CODE_INPUT_CUSTOM_MOOD
                    );
                    dialog.setTargetFragment(this, REQUEST_CODE_INPUT_CUSTOM_MOOD);
                    dialog.show(getFragmentManager(), null);
                    return;
                }
                final String newMood;
                if (position == 0) {
                    newMood = "+";
                } else {
                    newMood = ((TextView) view12).getText().toString();
                }
                jot = new WrappedJot(jot) {
                    @Override
                    public String mood() {
                        return newMood;
                    }
                };
                moodText.setText(newMood);
                moodDialog.dismiss();
            });
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        attachFab(new FabBehavior() {
            @Override
            public int icon() {
                return R.drawable.ic_save;
            }

            @Override
            public void onClick() {
                save();
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        detachFab();
    }

    private void updateAddress(Location location) {
        backgroundHandler.post(() -> {
            final String value = new LocationString(
                new LocationAddress(locationText.getContext(), location).value()
            ).value();
            locationText.post(() -> locationText.setText(value));
        });
    }

    private void pickLocation() {
        LocationPickerActivity.Builder build = new LocationPickerActivity.Builder();
        if (!Arrays.equals(jot.location(), new double[]{MIN_VALUE, MIN_VALUE})
            && !Arrays.equals(jot.location(), new double[]{0.0, 0.0})) {
            build.withLocation(jot.location()[1], jot.location()[0]);
        }
        startActivityForResult(
            build.build(getContext()),
            REQUEST_CODE_LOCATION_PICKER
        );
    }

    private void tagChipClicked(Chip tagChip) {
        new AlertDialog.Builder(getContext())
            .setTitle(tagChip.getText().toString())
            .setAdapter(new ArrayAdapter<>(
                getContext(),
                android.R.layout.simple_list_item_1,
                new String[]{
                    getString(R.string.jots),
                    getString(R.string.delete)
                }
            ), (dialog1, which1) -> onTagOptionClicked(tagChip, which1))
            .show();
    }

    private void onTagOptionClicked(Chip tagChip, int which1) {
        switch (which1) {
            case 0:
                // @todo: confirm for dicard changes
                nextPage(JotListFragment.newInstanceByJotIds(
                    getString(R.string.tag_title, tagChip.getText().toString()),
                    new QueriedJots(new JotsByTagId(db,
                        new ConstSource<>(
                            new CreatedTagByName(db, tagChip.getText().toString()).value().id()
                        ))
                    ).value().stream().mapToLong(value -> value.id()).toArray()));
                break;
            case 1:
                new AlertDialog.Builder(getContext())
                    .setTitle(tagChip.getText().toString())
                    .setMessage(getString(R.string.delete))
                    .setPositiveButton(R.string.confirm, (dialog2, which2) ->
                        chipGroup.removeView(tagChip)
                    ).setNegativeButton(R.string.cancel, null)
                    .show();
                break;
            default:
                break;
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
        if (item.getItemId() == R.id.menuItem_delete) {
            preferDelete();
        }
        return false;
    }

    private void save() {
        jot = new PostedJot(db, jot).value();
        final List<Attachment> attachments = new QueriedAttachments(
            new AttachmentsByJotId(db, jot.id(), true)
        ).value();
        attachmentAdapter.exportUri().forEach(uri -> {
            boolean hasItem = false;
            List<Attachment> exist = new ArrayList<>();
            for (Attachment attachment : attachments) {
                if (attachment.uri().equals(uri.toString())) {
                    hasItem = true;
                    if (attachment.deleted()) {
                        new UpdateAttachment(
                            db,
                            new WrappedAttachment(attachment) {
                                @Override
                                public boolean deleted() {
                                    return false;
                                }
                            }
                        ).fire();
                    }
                    exist.add(attachment);
                }
            }
            attachments.removeAll(exist);
            if (!hasItem) {
                new NewAttachment(db, uri.toString(), jot.id()).value();
            }
        });
        attachments.forEach((attachment) ->
            new RemovalAttachment(db, attachment.id()).fire()
        );
        final Map<Long, Tag> dbTags = new QueriedTags(new TagsByJotId(db, jot.id()))
            .value()
            .stream()
            .collect(Collectors.toMap(Tag::id, tag -> tag));
        for (int i = 0; i < chipGroup.getChildCount(); i++) {
            Tag tagOnView = ((Tag) chipGroup.getChildAt(i).getTag());
            if (!dbTags.containsKey(tagOnView.id())) {
                // update JOT TAG
                new NewJotTag(
                    db,
                    new ConstSource<>(jot.id()),
                    new ConstSource<>(tagOnView.id())
                ).fire();
            } else {
                dbTags.remove(tagOnView.id());
            }
        }
        dbTags.forEach((aLong, tag) -> new JotTagRemoval(db, jot.id(), tag.id()).fire());
        SyncService.enqueue(getContext());
        final Intent intent = new Intent();
        intent.setData(
            Uri.parse(new JotUri(BuildConfig.URI_HOST, jot).value().toASCIIString())
        );
        sendResult(0, RESULT_OK, intent);
    }

    private void deleteFlow() {
        new AlertDialog.Builder(getContext())
            .setTitle(R.string.delete)
            .setPositiveButton(R.string.confirm, (dialog, which) -> {
                new JotRemoval(db, jot.id()).fire();
                getFragmentManager().popBackStack();
            })
            .setNegativeButton(R.string.cancel, null)
            .show();
    }

    private void discardFlow() {
        if (jot.id() <= 0) {
            try {
                new JotRemoval(db, jot.id()).fire();
            } catch (Exception e) {
                // make sure not have actually inserted into db
                e.printStackTrace();
            }
        }
        if (jot instanceof WrappedJot) { // modified
            new AlertDialog.Builder(getContext())
                .setTitle(R.string.discard)
                .setPositiveButton(R.string.confirm, (dialog, which) -> {
                    getFragmentManager().popBackStack();
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
        } else {
            getFragmentManager().popBackStack();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
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
            locationText.setText(address == null ? "" : new LocationString(address).value());
            loadEmbedMapByJot();
        } else if (requestCode == REQUEST_CODE_PICK_FILE && resultCode == RESULT_OK) {
            if (data.getData() != null) {
                addAttachment(data.getData());
            } else {
                ClipData clip = data.getClipData();
                for (int i = 0; i < clip.getItemCount(); i++) {
                    addAttachment(clip.getItemAt(i).getUri());
                }
            }
        } else if (requestCode == REQUEST_CODE_INPUT_CUSTOM_MOOD && resultCode == RESULT_OK) {
            final String newMoodRaw = data.getStringExtra("INPUT_FIELD");
            final String newMood;
            if (newMoodRaw.length() > 1) {
                newMood = newMoodRaw.substring(0, 2);
            } else {
                newMood = "+";
            }
            moodText.setText(newMood);
            jot = new WrappedJot(jot) {
                @Override
                public String mood() {
                    return newMood;
                }
            };
        } else if (requestCode == REQUEST_CODE_ATTACHMENT_DIALOG){
            ArrayList<Uri> uris = data.getParcelableArrayListExtra("ARG_ATTACHMENT_URI");
            attachmentAdapter.loadAttachments(uris);
        }
    }

    private void addAttachment(Uri uri) {
        getContext().getContentResolver().takePersistableUriPermission(
            uri,
            FLAG_GRANT_READ_URI_PERMISSION
        );
        final String mimeType = new UriMimeType(
            getContext(),
            uri.toString()
        ).value();
        if (mimeType.startsWith("image")) {
            if (Arrays.equals(jot.location(), new double[]{MIN_VALUE, MIN_VALUE})
                || Arrays.equals(jot.location(), new double[]{0.0, 0.0})
            ) {
                loadLocationByExif(uri);
            }
            attachmentAdapter.append(uri);
        } else if (mimeType.startsWith("video")) {
            attachmentAdapter.append(uri);
        } else if (mimeType.startsWith("audio")) {
            attachmentAdapter.append(uri);
        } else {
            Alert.Companion.newInstance(
                REQUEST_CODE_ALERT,
                getString(R.string.not_supported_file)
            ).show(getChildFragmentManager(), null);
        }
    }

    private void loadLocationByExif(Uri data) {
        try {
            final ExifInterface exif = new ExifInterface(
                getContext().getContentResolver().openInputStream(data)
            );
            final double[] latLong = exif.getLatLong();
            if (latLong == null) {
                return;
            }
            jot = new WrappedJot(jot) {
                @Override
                public double[] location() {
                    return new double[]{latLong[1], latLong[0]};
                }
            };
            Location location = new Location("constant");
            location.setLatitude(latLong[0]);
            location.setLongitude(latLong[1]);
            updateAddress(location);
            loadEmbedMapByJot();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadEmbedMapByJot() {
        FragmentManager mgr = getChildFragmentManager();
        if (!Arrays.equals(jot.location(), new double[]{MIN_VALUE, MIN_VALUE})
            && !Arrays.equals(jot.location(), new double[]{0.0, 0.0})) {
            mgr.beginTransaction().replace(
                R.id.jot_embedMapContainer,
                EmbedMapFragment.newInstance(
                    jot.location()[0],
                    jot.location()[1]
                )
            ).commit();
            getView().findViewById(R.id.jot_embedMapContainer).setVisibility(View.VISIBLE);
        } else {
            Fragment map = mgr.findFragmentById(R.id.jot_embedMapContainer);
            if (map != null) {
                mgr.beginTransaction().remove(map).commit();
            }
            getView().findViewById(R.id.jot_embedMapContainer).setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onBackPress() {
        if (jot instanceof WrappedJot) {
            discardFlow();
            return true;
        } else {
            return false;
        }
    }

    private void preferDelete() {
        if (jot.id() < 0) {
            discardFlow();
        } else {
            deleteFlow();
        }
    }
}
