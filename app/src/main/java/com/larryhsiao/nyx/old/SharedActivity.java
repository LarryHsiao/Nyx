package com.larryhsiao.nyx.old;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.MimeTypeMap;
import androidx.annotation.Nullable;
import androidx.exifinterface.media.ExifInterface;
import com.larryhsiao.nyx.R;
import com.larryhsiao.nyx.core.jots.ConstJot;
import com.larryhsiao.nyx.core.jots.Jot;
import com.larryhsiao.nyx.old.attachments.TempAttachmentFile;
import com.larryhsiao.nyx.old.base.JotActivity;
import com.larryhsiao.nyx.old.jot.JotContentFragment;
import com.linkedin.urls.Url;
import com.linkedin.urls.detection.UrlDetector;
import com.silverhetch.aura.images.exif.ExifAttribute;
import com.silverhetch.aura.images.exif.ExifUnixTimeStamp;
import com.silverhetch.aura.uri.UriMimeType;
import com.larryhsiao.clotho.file.FileText;
import com.larryhsiao.clotho.file.ToFile;
import com.larryhsiao.clotho.source.ConstSource;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;
import java.util.UUID;
import java.util.stream.Collectors;

import static androidx.exifinterface.media.ExifInterface.TAG_DATETIME_ORIGINAL;
import static com.larryhsiao.nyx.NyxApplication.URI_FILE_TEMP_PROVIDER;
import static com.linkedin.urls.detection.UrlDetectorOptions.Default;
import static java.lang.Double.MIN_VALUE;
import static java.util.Calendar.ZONE_OFFSET;

/**
 * Activity that launched by third-party apps to touch jot from the share list.
 */
public class SharedActivity extends JotActivity {
    private static final int REQUEST_CODE_NEW_JOT = 1000;
    private final MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();

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
        ArrayList<String> attachmentList = new UrlDetector(content, Default)
            .detect()
            .stream()
            .map(Url::toString)
            .collect(Collectors.toCollection(ArrayList::new));
        final ClipData clipData = intent.getClipData();
        for (int i = 0; i < clipData.getItemCount(); i++) {
            final ClipData.Item item = clipData.getItemAt(i);
            final Uri uri = item.getUri();
            if (uri == null) {
                continue;
            }
            final String contentType = new UriMimeType(this, uri.toString()).value();
            if ("text/plain".equals(contentType)) {
                // @todo #0 Read file off main thread
                content = readText(item.getUri());
            } else if (
                contentType.startsWith("image/") ||
                    contentType.startsWith("video/") ||
                    contentType.startsWith("audio/")
            ) {
                try {
                    final String ext = mimeTypeMap.getExtensionFromMimeType(contentType);
                    final String fileName = UUID.randomUUID().toString() + "." + ext;
                    new ToFile(
                        getContentResolver().openInputStream(item.getUri()),
                        new TempAttachmentFile(this, fileName).value(),
                        integer -> null
                    ).fire();
                    attachmentList.add(URI_FILE_TEMP_PROVIDER + fileName);
                } catch (Exception ignore) {
                    ignore.printStackTrace();
                }
            }
        }
        nextPage(
            JotContentFragment.newInstance(
                initJotByExif(attachmentList),
                new ArrayList<>(attachmentList),
                REQUEST_CODE_NEW_JOT
            )
        );
    }

    private Jot initJotByExif(List<String> attachments) {
        try {
            if (attachments.size() > 0) {
                final ExifInterface exif = new ExifInterface(
                    getContentResolver().openInputStream(Uri.parse(attachments.get(0)))
                );
                return new ConstJot(
                    -1,
                    "",
                    "",
                    exifTime(exif),
                    exifGeometry(exif),
                    "",
                    1,
                    false
                );
            } else {
                return new ConstJot();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ConstJot();
        }
    }

    private long exifTime(ExifInterface exif) {
        try {
            final Long time = new ExifUnixTimeStamp(
                new ExifAttribute(
                    new ConstSource<>(exif),
                    TAG_DATETIME_ORIGINAL
                )
            ).value();
            TimeZone tz = TimeZone.getDefault();
            return time - tz.getOffset(ZONE_OFFSET);
        } catch (Exception e) {
            e.printStackTrace();
            return System.currentTimeMillis();
        }
    }

    private double[] exifGeometry(ExifInterface exif) {
        try {
            return new double[]{exif.getLatLong()[1], exif.getLatLong()[0]};
        } catch (Exception e) {
            e.printStackTrace();
            return new double[]{MIN_VALUE, MIN_VALUE};
        }
    }

    private String readText(Uri uri) {
        try {
            File temp = new TempAttachmentFile(this, "temp.txt").value();
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
