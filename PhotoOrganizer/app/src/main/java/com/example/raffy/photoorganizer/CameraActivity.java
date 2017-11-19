package com.example.raffy.photoorganizer;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.SparseArray;
import android.widget.Toast;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;

import java.lang.ref.WeakReference;

/**
 * Use this dummy activity to wrap methods regarding the camera intent.
 */

public class CameraActivity extends AppCompatActivity {

    static final private int REQUEST_IMAGE_CAPTURE = 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
                //mImageView.setImageBitmap(imageBitmap);
                new ExamineImageTask(this).execute(imageBitmap);
            }
            finish();
        }
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
            FaceDetector faceDetector = new FaceDetector.Builder(context.get())
                    .setTrackingEnabled(false)
                    .build();
            BarcodeDetector codeDetector = new BarcodeDetector.Builder(context.get())
                    .setBarcodeFormats(Barcode.QR_CODE)
                    .build();
            for (Bitmap bitmap : bitmaps) {
                float ratio = 1200.0f / Math.max(bitmap.getWidth(), bitmap.getHeight());
                final Bitmap scaled = Bitmap.createScaledBitmap(bitmap, Math.round(bitmap.getWidth() * ratio), Math.round(bitmap.getHeight() * ratio), false);
                Frame frame = new Frame.Builder().setBitmap(scaled).build();
                final SparseArray<Face> faces = faceDetector.detect(frame);
                final SparseArray<Barcode> barcodes = codeDetector.detect(frame);
                context.get().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Boolean hasBarcodes = barcodes.size() > 1;
                        Boolean hasFaces = faces.size() > 1;
                        // TODO
                        String temp = String.format("Barcodes: %s, faces: %s. TODO put image somewhere",
                                hasBarcodes, hasFaces);
                        Toast.makeText(context.get(), temp, Toast.LENGTH_LONG).show();
                    }
                });
            }
            return null;
        }
    }
}
