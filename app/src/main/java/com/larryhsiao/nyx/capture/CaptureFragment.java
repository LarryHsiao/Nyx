package com.larryhsiao.nyx.capture;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.camera.core.*;
import androidx.camera.core.ImageCapture.OutputFileResults;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import com.google.common.util.concurrent.ListenableFuture;
import com.larryhsiao.nyx.R;
import com.larryhsiao.nyx.attachments.TempAttachmentFile;
import com.silverhetch.aura.AuraFragment;
import com.silverhetch.aura.location.LocationService;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static android.Manifest.permission.*;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;
import static android.widget.Toast.LENGTH_SHORT;
import static android.widget.Toast.makeText;
import static androidx.camera.core.CameraSelector.*;

/**
 * Fragment to show preview of camera for capturing photo.
 */
public class CaptureFragment extends AuraFragment implements ServiceConnection {
    private static final String[] REQUIRED_PERMISSIONS = new String[]{
        CAMERA, WRITE_EXTERNAL_STORAGE, ACCESS_FINE_LOCATION
    };
    private static final String ARG_REQUEST_CODE = "ARG_REQUEST_CODE";
    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    private CameraSelector cameraSelector = new CameraSelector.Builder().build();
    private Integer lenFacing = LENS_FACING_BACK;
    private ImageCapture imgCapture;
    private ExecutorService cameraExecutor;
    private Location currentLocation = null;

    public static Fragment newInstance(int requestCode) {
        Fragment frag = new CaptureFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_REQUEST_CODE, requestCode);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        cameraProviderFuture.cancel(false);
    }

    @Nullable
    @Override
    public View onCreateView(
        @NonNull LayoutInflater inflater,
        @Nullable ViewGroup container,
        @Nullable Bundle savedInstanceState
    ) { return inflater.inflate(R.layout.page_capture, container, false); }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        cameraExecutor = Executors.newSingleThreadExecutor();
        if (allPermissionsGranted()) {
            requireContext().bindService(
                new Intent(requireContext(), LocationService.class),
                this,
                Activity.BIND_AUTO_CREATE
            );
            loadView();
        } else {
            requestPermissionsByObj(REQUIRED_PERMISSIONS);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        cameraExecutor.shutdown();
        try {
            requireContext().unbindService(this);
        } catch (Exception ignore) {
        }
    }

    @Override
    public void onPermissionGranted() {
        super.onPermissionGranted();
        loadView();
    }

    private void loadView() {
        cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext());
        cameraProviderFuture.addListener(() -> {
            try {
                initCamera(cameraProviderFuture.get());
            } catch (Exception e) {
                e.printStackTrace();
                showError();
            }
        }, ContextCompat.getMainExecutor(requireContext()));
        requireView().findViewById(R.id.capture_trigger).setOnClickListener(v -> {
            File tempFile;
            imgCapture.takePicture(
                new ImageCapture.OutputFileOptions.Builder(
                    tempFile = new TempAttachmentFile(
                        requireContext(),
                        System.currentTimeMillis() + ".jpg"
                    ).value()
                ).setMetadata(metadata()).build(),
                cameraExecutor,
                new ImageCapture.OnImageSavedCallback() {
                    @Override
                    public void onImageSaved(@NotNull OutputFileResults outputFileResults) {
                        sendResult(
                            requireArguments().getInt(ARG_REQUEST_CODE),
                            Activity.RESULT_OK,
                            data(tempFile)
                        );
                    }

                    @Override
                    public void onError(@NotNull ImageCaptureException exception) {
                        requireView().post(() -> {
                            showError();
                        });
                    }
                }
            );
        });
    }

    private void showError() {
        sendResult(
            requireArguments().getInt(ARG_REQUEST_CODE),
            Activity.RESULT_CANCELED,
            new Intent()
        );
        makeText(
            getContext(),
            R.string.appError_unknown,
            LENGTH_SHORT
        ).show();
    }

    private Intent data(File file) {
        Intent intent = new Intent();
        intent.setData(Uri.parse(file.toURI().toASCIIString()));
        return intent;
    }

    private ImageCapture.Metadata metadata() {
        ImageCapture.Metadata metadata = new ImageCapture.Metadata();
        metadata.setReversedHorizontal(lenFacing == LENS_FACING_FRONT);
        if (currentLocation != null) {
            metadata.setLocation(currentLocation);
        }
        return metadata;
    }

    private void initCamera(ProcessCameraProvider cameraProvider) throws Exception {
        if (cameraProvider.hasCamera(DEFAULT_BACK_CAMERA)) {
            lenFacing = LENS_FACING_BACK;
        } else if (cameraProvider.hasCamera(DEFAULT_FRONT_CAMERA)) {
            lenFacing = LENS_FACING_FRONT;
        } else {
            throw new RuntimeException("No camera available");
        }
        cameraSelector = new CameraSelector.Builder()
            .requireLensFacing(lenFacing)
            .build();

        Preview preview = new Preview.Builder().build();
        PreviewView previewView = requireView().findViewById(R.id.photoCapture_preview);
        preview.setSurfaceProvider(previewView.createSurfaceProvider());

        DisplayMetrics metrics = new DisplayMetrics();
        requireView().getDisplay().getRealMetrics(metrics);

        imgCapture = new ImageCapture.Builder()
            .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
            .setTargetAspectRatio(AspectRatio.RATIO_16_9)
            .setTargetRotation(requireView().getDisplay().getRotation())
            .build();

        cameraProvider.unbindAll();
        cameraProvider.bindToLifecycle(this, cameraSelector, imgCapture, preview);
    }

    private boolean allPermissionsGranted() {
        for (String permission : REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(requireContext(), permission) !=
                PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        ((LocationService.Binder) service).location().observe(
            this,
            location -> currentLocation = location
        );
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
    }
}
