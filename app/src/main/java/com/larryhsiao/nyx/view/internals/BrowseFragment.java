package com.larryhsiao.nyx.view.internals;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.larryhsiao.nyx.R;
import com.larryhsiao.nyx.view.diary.attachment.ViewAttachmentIntent;
import com.silverhetch.aura.AuraFragment;
import com.silverhetch.clotho.utility.comparator.FileComparator;
import com.silverhetch.clotho.utility.comparator.StringComparator;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.view.KeyEvent.KEYCODE_BACK;
import static java.util.Objects.requireNonNull;

/**
 * Fragment that browse the file system.
 */
public class BrowseFragment extends AuraFragment {
    private final static String KEY_PATH = "KEY_PATH";
    private File root = null;
    private File current = null;
    private final BrowseAdapter adapter = new BrowseAdapter(this::onFileClicked);

    public static Fragment newInstance(final String path) {
        final BrowseFragment frag = new BrowseFragment();
        final Bundle args = new Bundle();
        args.putString(KEY_PATH, path);
        frag.setArguments(args);
        return frag;
    }

    private Boolean onFileClicked(File file) {
        if (file.isDirectory()) {
            if ("..".equals(file.getName())){
                // Little trick for file back stack, the parent of "path/to/.." is same as "path/to"
                // So we have to get twice parent file to get the actual parent "path/"
                loadDirectory(file.getParentFile().getParentFile());
            }else {
                loadDirectory(file);
            }
        } else {
            openFile(file);
        }
        return true;
    }

    private void openFile(File file) {
        final Context context = getContext();
        if (context == null) {
            return;
        }
        try {
            startActivity(new ViewAttachmentIntent(context, Uri.fromFile(file)).value());
        } catch (Exception e) {
            new AlertDialog.Builder(context)
                    .setMessage(R.string.failed_to_open_file)
                    .show();
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Bundle args = getArguments();
        assert args != null : "Missing arguments";
        root = new File(requireNonNull(args.getString(KEY_PATH)));
        current = new File("");

        requestPermissionsByObj(new String[]{WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE});
    }

    @Override
    public void onPermissionGranted() {
        super.onPermissionGranted();
        loadDirectory(root);
    }

    private boolean loadDirectory(File dir) {
        try {
            if (current.getCanonicalPath().equals(dir.getCanonicalPath()) ||
                    !dir.getCanonicalPath().startsWith(root.getCanonicalPath())
            ) {
                return false;
            }
            current = dir;
            final File[] fileArr = dir.listFiles();
            assert fileArr != null : "No file available";
            final List<File> files = new ArrayList<>();
            if (!root.getCanonicalPath().equals(dir.getCanonicalPath())) {
                files.add(new File(current, ".."));
            }
            files.addAll(Arrays.asList(fileArr));
            files.sort(new FileComparator(new Comparator<File>() {
                private final StringComparator comparator = new StringComparator();

                @Override
                public int compare(File o1, File o2) {
                    return comparator.compare(o2.getName(), o1.getName());
                }
            }));
            adapter.updateFiles(files);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(
                R.layout.fragment_browse_list,
                container,
                false
        );
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        final RecyclerView list = view.findViewById(R.id.browse_list);
        list.setLayoutManager(new LinearLayoutManager(view.getContext()));
        list.setAdapter(adapter);
        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener((v, keyCode, event) -> {
            if (keyCode == KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
                return loadDirectory(requireNonNull(current.getParentFile()));
            }
            return false;
        });
    }
}
