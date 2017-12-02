package com.example.raffy.photoorganizer;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;

import java.lang.ref.WeakReference;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;

import static com.example.raffy.photoorganizer.QRCameraPreview.getCameraInstance;

/**
 * Use this dummy activity to wrap methods regarding the join group camera intent.
 *
 * This camera intent closes automatically when it recognizes a QR code.
 */

public class JoinActivity extends AppCompatActivity {

    public static final int PERMISSIONS_REQUEST_CAMERA = 1;
    private Camera mCamera;
    private QRCameraPreview mPreview;
    private Timer mTimer;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.join_group_layout);
        setTitle(R.string.join_group_hint);

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
    public void onDetachedFromWindow() {
        mTimer.cancel();
        super.onDetachedFromWindow();
    }

    @Override
    protected void onDestroy() {
        mTimer.cancel();
        super.onDestroy();
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
                Bitmap bitmap = mPreview.getBitmap();
                if (bitmap != null)
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
                        if (barcodes.size() > 0) {
                            String qr = barcodes.valueAt(0).displayValue;
                            String groupName = qr.split("-")[0];
                            String joinCode = qr.split("-")[1];
                            startJoinGroupAction(context.get(), groupName, joinCode);
                            Toast.makeText(context.get(), "Barcode found", Toast.LENGTH_SHORT).show();
                            context.get().finish();
                        }
                    }
                });
            }
            return null;
        }
    }

    private static void startJoinGroupAction(final Activity context, final String group_name, final String join_code) {
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return;
        final ProgressDialog progress = new ProgressDialog(context);
        progress.show();
        // get token and start progress
        user.getIdToken(true).addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
            @Override
            public void onComplete(@NonNull Task<GetTokenResult> task) {
                String token = task.getResult().getToken();
                RequestBody body = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("token", token)
                        .addFormDataPart("join_token", join_code)
                        .addFormDataPart("group_name", group_name)
                        .addFormDataPart("user", user.getUid())
                        .build();
                Request request = new Request.Builder()
                        .url("http://10.0.2.2:5000/join_group")  // TODO
                        .post(body)
                        .build();
                new ApiHttp(context, progress).execute(request);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("!!!", e.getMessage());
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
