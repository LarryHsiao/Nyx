package com.larryhsiao.nyx.attachments;

import android.app.Activity;
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
import com.larryhsiao.nyx.jot.AttachmentAdapter;
import com.silverhetch.aura.view.dialog.FullScreenDialogFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Fragment that shows the attachments.
 */
public class AttachmentsFragment extends FullScreenDialogFragment {
    private final static String ARG_ATTACHMENT_URI = "ARG_ATTACHMENT_URI";

    private List<Uri> uris;
    private AttachmentAdapter adapter;

    public static FullScreenDialogFragment newInstance(List<Uri> uris) {
        FullScreenDialogFragment frag = new AttachmentsFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(ARG_ATTACHMENT_URI, new ArrayList<>(uris));
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
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.list, container, false);
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
            Activity.RESULT_OK,
            intent
        );
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView listView = ((RecyclerView) view);
        listView.setBackgroundColor(Color.BLACK);
        listView.setAdapter(adapter = new AttachmentAdapter(view.getContext()));
        listView.setLayoutManager(new GridLayoutManager(view.getContext(), 2));
        adapter.loadAttachments(uris);
    }
}
