package com.larryhsiao.nyx.old.capture;

import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.exifinterface.media.ExifInterface;
import com.larryhsiao.nyx.R;
import com.larryhsiao.nyx.core.jots.Jot;
import com.larryhsiao.nyx.core.jots.NewJot;
import com.larryhsiao.nyx.old.base.JotActivity;
import com.larryhsiao.nyx.old.util.exif.ExifLocation;
import com.larryhsiao.aura.view.activity.Fullscreen;
import com.larryhsiao.aura.view.bitmap.ResizedImage;
import com.larryhsiao.clotho.io.ProgressedCopy;
import com.larryhsiao.clotho.source.ConstSource;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileInputStream;
import java.net.URI;
import java.util.Calendar;

import static android.provider.MediaStore.EXTRA_OUTPUT;
import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static java.lang.Double.MIN_VALUE;

/**
 * Activity to handle capture photo for new Nyx.
 */
public class CaptureActivity extends JotActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FrameLayout root = new FrameLayout(this);
        root.setLayoutParams(new FrameLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT));
        root.setId(View.generateViewId());
        setContentView(root);

        getSupportFragmentManager().beginTransaction()
            .replace(root.getId(), CaptureFragment.newInstance(1000))
            .commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        new Fullscreen(this).value();
    }

    @Override
    public void onFragmentResult(int requestCode, int resultCode, @NotNull Intent data) {
        super.onFragmentResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        String capturedFileUri = data.getData().toString();
        if (getCallingActivity() != null) {
            File capturedFile = new File(URI.create(capturedFileUri));
            Uri extraOutput = getIntent().getParcelableExtra(EXTRA_OUTPUT);
            final Intent result;
            if (extraOutput == null || extraOutput.toString().isEmpty()) {
                result = thumbnailResult(capturedFile);
            } else {
                result = exportFile(capturedFile, extraOutput);
            }
            capturedFile.delete();
            setResult(RESULT_OK, result);
            finish();
        } else {
            Location location = parseLocation(data);
//            List<Jot> exist = new QueriedJots(
//                new JotsByTimeSpace(
//                    db,
//                    System.currentTimeMillis(),
//                    new CircleByRange(
//                        new double[]{location.getLongitude(), location.getLatitude()},
//                        new MeterDelta(15)
//                    )
//                )
//            ).value();
//            final Jot jot;
//            if (exist.size() > 0) {
//                jot = exist.get(0);
//            } else {
//                jot = newJot(location);
//            }
//            new NewAttachments(db, jot.id(), new String[]{capturedFileUri}).value();
        }
    }

    private Location parseLocation(Intent data) {
        Location location = new Location("constant");
        location.setLatitude(MIN_VALUE);
        location.setLongitude(MIN_VALUE);
        try {
            location = new ExifLocation(
                new ExifInterface(
                    new FileInputStream(
                        new File(
                            URI.create(data.getData().toString())
                        )
                    )
                )
            ).value();
        } catch (Exception ignore) {
        }
        return location;
    }

    private Intent thumbnailResult(File capturedFile) {
        Intent result = new Intent();
        result.putExtra(
            "data",
            new ResizedImage(
                new ConstSource<>(
                    BitmapFactory.decodeFile(
                        capturedFile.getAbsolutePath()
                    )
                ),
                250
            ).value()
        );
        return result;
    }

    private Intent exportFile(File capturedFile, Uri extraOutput) {
        try {
            new ProgressedCopy(
                new FileInputStream(capturedFile),
                getContentResolver().openOutputStream(extraOutput),
                4 * 1024 * 1024,
                integer -> null
            ).value();
        } catch (Exception e) {
            Toast.makeText(this, R.string.appError_unknown, Toast.LENGTH_SHORT).show();
        }
        return new Intent();
    }

    private Jot newJot(Location location) {
        return new NewJot(
            db,
            "",
            "",
            new double[]{location.getLongitude(), location.getLatitude()},
            Calendar.getInstance(),
            "",
            false
        ).value();
    }
}
