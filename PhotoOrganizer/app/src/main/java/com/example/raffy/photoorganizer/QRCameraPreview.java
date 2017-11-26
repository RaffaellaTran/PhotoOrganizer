package com.example.raffy.photoorganizer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.TextureView;

import java.io.IOException;
import java.util.List;

/**
 * Source: https://developer.android.com/guide/topics/media/camera.html
 */

public class QRCameraPreview extends TextureView implements TextureView.SurfaceTextureListener {
    private Camera mCamera;

    public QRCameraPreview(Context context) {
        super(context);
    }

    public QRCameraPreview(Context context, Camera camera) {
        super(context);
        mCamera = camera;
        setSurfaceTextureListener(this);
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i, int i1) {
        // The Surface has been created, now tell the camera where to draw the preview.
        Log.e("!!!", "toimiiko?");
        try {
            mCamera.setPreviewTexture(surfaceTexture);
            mCamera.startPreview();
        } catch (IOException e) {
            Log.d("QR", "Error setting camera preview: " + e.getMessage());
        }
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
        // empty. Take care of releasing the Camera preview in your activity.
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
        // If your preview can change or rotate, take care of those events here.
        // Make sure to stop the preview before resizing or reformatting it.

        // stop preview before making changes
        try {
            mCamera.stopPreview();
        } catch (Exception e){
            // ignore: tried to stop a non-existent preview
        }

        // set preview size and make any resize, rotate or
        // reformatting changes here

        // start preview with new settings
        try {
            mCamera.setPreviewTexture(surfaceTexture);
            mCamera.startPreview();

        } catch (Exception e){
            Log.d("QR", "Error starting camera preview: " + e.getMessage());
        }

    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i1) {
        // If your preview can change or rotate, take care of those events here.
        // Make sure to stop the preview before resizing or reformatting it.

        // stop preview before making changes
        try {
            mCamera.stopPreview();
        } catch (Exception e){
            // ignore: tried to stop a non-existent preview
        }

        // set preview size and make any resize, rotate or
        // reformatting changes here

        // start preview with new settings
        try {
            mCamera.setPreviewTexture(surfaceTexture);
            mCamera.startPreview();

        } catch (Exception e){
            Log.d("QR", "Error starting camera preview: " + e.getMessage());
        }
    }

    /**
     * A safe way to get an instance of the Camera object.
     * NOTE! Remember to request permission before firing this!
     */

    public static Camera getCameraInstance(){
        Camera c = null;
        try {
            c = Camera.open(); // attempt to get a Camera instance
            // https://stackoverflow.com/questions/27021347/android-surfaceview-preview-blurry
            Camera.Parameters parameters = c.getParameters();
            List<String> focusModes = parameters.getSupportedFocusModes();
            if (focusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
                parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
            } else if (focusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
                parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
            }
            c.setParameters(parameters);
        }
        catch (Exception e){
            // Camera is not available (in use or does not exist)
        }
        return c; // returns null if camera is unavailable
    }
}
