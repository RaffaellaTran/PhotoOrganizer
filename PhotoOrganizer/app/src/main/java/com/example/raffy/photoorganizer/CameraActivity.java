package com.example.raffy.photoorganizer;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseArray;
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

import java.io.ByteArrayOutputStream;
import java.lang.ref.WeakReference;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * Use this dummy activity to wrap methods regarding the camera intent.
 */

public class CameraActivity extends AppCompatActivity {

    static final private int REQUEST_IMAGE_CAPTURE = 1;
    private SettingsHelper settings;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        settings = new SettingsHelper(getApplicationContext());

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE) {
            if (resultCode == RESULT_OK) {
                Bundle extras = data.getExtras();
                @SuppressWarnings("ConstantConditions")
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                new ExamineImageTask(this).execute(imageBitmap);
            }
        }
    }

    /**
     * Use a static class to prevent leaks.
     */

    private static class ExamineImageTask extends AsyncTask<Bitmap, Void, Void> {

        private WeakReference<CameraActivity> context;

        ExamineImageTask(CameraActivity context) {
            this.context = new WeakReference<>(context);
        }

        @Override
        protected Void doInBackground(Bitmap... bitmaps) {
            BarcodeDetector codeDetector = new BarcodeDetector.Builder(context.get())
                    .setBarcodeFormats(Barcode.QR_CODE)
                    .build();
            for (final Bitmap bitmap : bitmaps) {
                float ratio = 1200.0f / Math.max(bitmap.getWidth(), bitmap.getHeight());
                final Bitmap scaled = Bitmap.createScaledBitmap(bitmap, Math.round(bitmap.getWidth() * ratio), Math.round(bitmap.getHeight() * ratio), false);
                Frame frame = new Frame.Builder().setBitmap(scaled).build();
                final SparseArray<Barcode> barcodes = codeDetector.detect(frame);
                context.get().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Boolean hasBarcodes = barcodes.size() > 0;

                        if (hasBarcodes) {
                            Toast.makeText(context.get(), "Barcodes found! ABORT!!!", Toast.LENGTH_LONG).show();
                            context.get().finish();
                            return;
                        }

                        final ProgressDialog progress = ApiHttp.getProgressDialog(context.get());
                        Group.getMyGroup(new Group.GetMyGroupResult() {
                            @Override
                            public void react(@Nullable Group group) {
                                progress.dismiss();
                                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                if (group != null && user != null) {
                                    Float ratio = null;
                                    switch (context.get().settings.getImageQuality()) {
                                        case "LOW":
                                            ratio = 640.0f / Math.max(bitmap.getWidth(), bitmap.getHeight());
                                            break;
                                        case "HIGH":
                                            ratio = 1280.0f / Math.max(bitmap.getWidth(), bitmap.getHeight());
                                            break;
                                        default:
                                            break;
                                    }
                                    if (ratio == null) {
                                        startUploadAction(group.getName(), user, bitmap);
                                    } else {
                                        Bitmap bitmap2 = Bitmap.createScaledBitmap(bitmap, Math.round(bitmap.getWidth() * ratio), Math.round(bitmap.getHeight() * ratio), false);
                                        startUploadAction(group.getName(), user, bitmap2);
                                    }
                                } else {
                                    if (group == null)
                                        Toast.makeText(context.get(), "You must be in a group to take photos!", Toast.LENGTH_LONG).show();
                                    else
                                        Toast.makeText(context.get(), "User null!", Toast.LENGTH_LONG).show();
                                    context.get().finish();
                                }
                            }
                        });
                    }
                });
            }
            return null;
        }

        private void startUploadAction(final String groupName, FirebaseUser user, final Bitmap bitmap) {
            final ProgressDialog progress = ApiHttp.getProgressDialog(context.get());
            // get token and start progress
            user.getIdToken(true).addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                @Override
                public void onComplete(@NonNull Task<GetTokenResult> task) {
                    MediaType MEDIA_TYPE_JPEG = MediaType.parse("image/jpeg");
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                    byte[] data = stream.toByteArray();

                    // backend apparently uses this to store locally -> randomize to prevent overrides
                    String tempFileName = Long.toString(System.currentTimeMillis());
                    String token = task.getResult().getToken();
                    if (token == null) token = "";
                    RequestBody body = new MultipartBody.Builder()
                            .setType(MultipartBody.FORM)
                            .addFormDataPart("token", token)
                            .addFormDataPart("group_name", groupName)
                            .addFormDataPart("imagefile", tempFileName, RequestBody.create(MEDIA_TYPE_JPEG, data))
                            .build();
                    Request request = new Request.Builder()
                            .post(body)
                            .url(SettingsHelper.BACKEND_URL + "/label")
                            .build();
                    new ApiHttp(context.get(), progress).execute(request);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e("!!!", e.getMessage());
                    Toast.makeText(context.get(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    progress.dismiss();
                    context.get().finish();
                }
            });
        }
    }
}
