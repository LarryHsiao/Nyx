package com.larryhsiao.nyx;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.Nullable;
import com.larryhsiao.nyx.base.JotActivity;
import com.larryhsiao.nyx.core.jots.ConstJot;
import com.larryhsiao.nyx.jot.JotContentFragment;
import com.silverhetch.aura.uri.UriMimeType;
import com.silverhetch.clotho.file.FileText;
import com.silverhetch.clotho.file.ToFile;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;

import static java.lang.Double.MIN_VALUE;

/**
 * Activity that launched by third-party apps to create jot from the share list.
 */
public class SharedActivity extends JotActivity {
    private static final int REQUEST_CODE_NEW_JOT = 1000;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.page_share);
        setupFabControl(findViewById(R.id.share_fab));
        setupPageControl(R.id.share_content);

        // https://img.youtube.com/vi/a5zoitIUp9c/hqdefault.jpg

        final Intent intent = getIntent();
        final String extraText = intent.getStringExtra(Intent.EXTRA_TEXT);
        String content = extraText == null ? "" : extraText;

        final ClipData clipData = intent.getClipData();
        ArrayList<String> list = new ArrayList<>();
        for (int i = 0; i < clipData.getItemCount(); i++) {
            final ClipData.Item item = clipData.getItemAt(i);
            final Uri uri = item.getUri();
            if (uri == null) {
                continue;
            }
            final String contentType = new UriMimeType(
                this,
                uri.toString()
            ).value();
            if ("text/plain".equals(contentType)) {
                // @todo #0 Read file off main thread
                content = readText(item.getUri());
            } else if (
                contentType.startsWith("image/") ||
                    contentType.startsWith("video/") ||
                    contentType.startsWith("audio/")
            ) {
                list.add(item.getUri().toString());
            }
        }
        nextPage(
            JotContentFragment.newInstance(
                new ConstJot(
                    -1,
                    content,
                    System.currentTimeMillis(),
                    new double[]{MIN_VALUE, MIN_VALUE},
                    "",
                    1,
                    false
                ),
                new ArrayList<>(list),
                REQUEST_CODE_NEW_JOT
            )
        );
    }

    private String readText(Uri uri) {
        try {
            File temp = Files.createTempFile("temp", "txt").toFile();
            new ToFile(
                getContentResolver().openInputStream(uri),
                temp,
                it -> null
            ).fire();

            return new FileText(temp).value();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
            .setTitle(R.string.discard)
            .setPositiveButton(R.string.confirm, (dialog, which) -> {
                setResult(RESULT_CANCELED);
                finish();
            })
            .setNegativeButton(R.string.cancel, null)
            .show();
    }

    @Override
    public void onFragmentResult(int requestCode, int resultCode, @NotNull Intent data) {
        if (requestCode == REQUEST_CODE_NEW_JOT) {
            // When received the result, the jot saved.
            setResult(RESULT_OK);
            finish();
        }
    }
}
