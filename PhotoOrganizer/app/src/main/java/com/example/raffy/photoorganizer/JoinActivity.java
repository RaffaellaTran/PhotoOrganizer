package com.example.raffy.photoorganizer;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseArray;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;

import java.lang.ref.WeakReference;
import java.util.Timer;
import java.util.TimerTask;

import static com.example.raffy.photoorganizer.QRCameraPreview.getCameraInstance;

/**
 * Use this dummy activity to wrap methods regarding the join group camera intent.
 */

public class JoinActivity extends AppCompatActivity {

    public static final int PERMISSIONS_REQUEST_CAMERA = 1;
    private Camera mCamera;
    private QRCameraPreview mPreview;
    private Timer mTimer;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.join_group_layout);

        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{android.Manifest.permission.CAMERA},
                        PERMISSIONS_REQUEST_CAMERA);
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.CAMERA},
                        PERMISSIONS_REQUEST_CAMERA);
            }
        } else {
            setPreview();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_CAMERA:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    setPreview();
                } else {
                    Toast.makeText(this, "Camera permission is denied!", Toast.LENGTH_LONG).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void setPreview() {
        // Create an instance of Camera
        mCamera = getCameraInstance();
        // Create our Preview view and set it as the content of our activity.
        mPreview = new QRCameraPreview(this, mCamera);
        FrameLayout preview = findViewById(R.id.camera_preview);
        preview.addView(mPreview);

        // Timer
        mTimer = new Timer();
        mTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Bitmap bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888);
                Canvas c = new Canvas(bitmap);
                mPreview.draw(c);
                new ExamineImageTask(JoinActivity.this).execute(bitmap);
            }
        }, 0, 1000);
    }

    /**
     * Use a static class to prevent leaks.
     */

    private static class ExamineImageTask extends AsyncTask<Bitmap, Void, Void> {

        private WeakReference<Activity> context;

        ExamineImageTask(Activity context) {
            this.context = new WeakReference<>(context);
        }

        @Override
        protected Void doInBackground(Bitmap... bitmaps) {
            BarcodeDetector codeDetector = new BarcodeDetector.Builder(context.get())
                    .setBarcodeFormats(Barcode.QR_CODE)
                    .build();
            for (Bitmap bitmap : bitmaps) {
                float ratio = 1200.0f / Math.max(bitmap.getWidth(), bitmap.getHeight());
                final Bitmap scaled = Bitmap.createScaledBitmap(bitmap, Math.round(bitmap.getWidth() * ratio), Math.round(bitmap.getHeight() * ratio), false);
                Frame frame = new Frame.Builder().setBitmap(scaled).build();
                final SparseArray<Barcode> barcodes = codeDetector.detect(frame);
                context.get().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // TODO
                        if (barcodes.size() > 0) {
                            Toast.makeText(context.get(), "Barcode found", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(context.get(), "No barcode", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
            return null;
        }
    }
}
