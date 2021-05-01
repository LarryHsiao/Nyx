package com.larryhsiao.nyx.old.attachments;

import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.larryhsiao.nyx.R;
import com.larryhsiao.nyx.old.jot.AttachmentAdapter;
import com.larryhsiao.aura.uri.UriMimeType;
import com.larryhsiao.aura.view.alert.Alert;
import com.larryhsiao.aura.view.dialog.FullScreenDialogFragment;

import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;
import static android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION;

/**
 * Fragment that shows the attachments.
 *
 * @todo #0 Attachment sorting by created time.
 * @todo #0 Attachment comment in pages.
 * @todo #0 Preloader from Glide for RecyclerView.
 */
public class AttachmentsFragment extends FullScreenDialogFragment {
    private static final String ARG_ATTACHMENT_URI = "ARG_ATTACHMENT_URI";
    private static final String ARG_SELECTED_URI = "ARG_SELECTED_URI";
    private static final int REQUEST_CODE_PICK_FILE = 1000;
    private static final int REQUEST_CODE_ALERT = 1001;

    private List<Uri> uris;
    private AttachmentAdapter adapter;

    public static FullScreenDialogFragment newInstance(
        List<Uri> uris,
        String selectedUri
    ) {
        FullScreenDialogFragment frag = new AttachmentsFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(ARG_ATTACHMENT_URI, new ArrayList<>(uris));
        bundle.putString(ARG_SELECTED_URI, selectedUri);
        frag.setArguments(bundle);
        return frag;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        uris = getArguments().getParcelableArrayList(ARG_ATTACHMENT_URI);
    }

    @Nullable
    @Override
    public View onCreateView(
        @NonNull LayoutInflater inflater,
        @Nullable ViewGroup container,
        @Nullable Bundle savedInstanceState
    ) {
        return inflater.inflate(R.layout.page_attachments, container, false);
    }

    @Override
    public void onCancel(@NonNull DialogInterface dialog) {
        super.onCancel(dialog);
        Intent intent = new Intent();
        intent.putParcelableArrayListExtra(
            ARG_ATTACHMENT_URI,
            new ArrayList<>(adapter.exportUri())
        );
        getTargetFragment().onActivityResult(
            getTargetRequestCode(),
            RESULT_OK,
            intent
        );
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView listView = view.findViewById(R.id.list);
        listView.setBackgroundColor(Color.BLACK);
        listView.setAdapter(adapter = new AttachmentAdapter(view.getContext()));
        final GridLayoutManager manager = new GridLayoutManager(view.getContext(), 2);
        manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (adapter.isFullSpan(position)) {
                    return manager.getSpanCount();
                } else {
                    return 1;
                }
            }
        });
        listView.setLayoutManager(manager);
        adapter.loadAttachments(uris);

        final String selected = requireArguments().getString(ARG_SELECTED_URI, "");
        if (selected != null && !selected.isEmpty()){
            listView.scrollToPosition(
                uris.indexOf(Uri.parse(selected))
            );
        }

        view.findViewById(R.id.attachments_plus).setOnClickListener(v -> {
            startActivityForResult(
                new AttachmentPickerIntent().value(),
                REQUEST_CODE_PICK_FILE
            );
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_PICK_FILE && resultCode == RESULT_OK) {
            if (data.getData() != null) {
                addAttachment(data.getData());
            } else {
                addMultiple(data);
            }
        }
    }

    private void addMultiple(Intent data) {
        ClipData clip = data.getClipData();
        for (int i = 0; i < clip.getItemCount(); i++) {
            addAttachment(clip.getItemAt(i).getUri());
        }
    }

    private void addAttachment(Uri uri) {
        try {
            getContext().getContentResolver().takePersistableUriPermission(
                uri,
                FLAG_GRANT_READ_URI_PERMISSION
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
        final String mimeType = new UriMimeType(
            requireContext(),
            uri.toString()
        ).value();
        if (mimeType.startsWith("image")) {
            adapter.append(uri);
        } else if (mimeType.startsWith("video")) {
            adapter.append(uri);
        } else if (mimeType.startsWith("audio")) {
            adapter.append(uri);
        } else {
            Alert.Companion.newInstance(
                REQUEST_CODE_ALERT,
                getString(R.string.not_supported_file)
            ).show(getChildFragmentManager(), null);
        }
    }
}
