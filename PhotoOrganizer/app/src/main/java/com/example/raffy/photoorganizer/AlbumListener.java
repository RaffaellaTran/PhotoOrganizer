package com.example.raffy.photoorganizer;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Adapter;
import android.widget.BaseAdapter;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

/**
 * Created by Anton on 3.12.2017.
 */
class AlbumListener implements ChildEventListener {

    interface AlbumEventListener {
        // Listener for custom callbacks
        void callback(GalleryImage image);
    }

    AlbumEventListener onNewImage;
    AlbumEventListener onUriFetched;

    public AlbumListener(AlbumEventListener onNewImage, AlbumEventListener onUriFetched) {
        this.onNewImage = onNewImage;
        this.onUriFetched = onUriFetched;
    }

    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
        final GalleryImage img = dataSnapshot.getValue(GalleryImage.class);
        //album.images.add(img);
        onNewImage.callback(img);

        // Get image download URIs
        for (final SettingsHelper.ImageQuality quality : SettingsHelper.ImageQuality.values()) {
            // Get image storage reference
            FirebaseStorage storage = SettingsHelper.getFirebaseStorage(quality);
            StorageReference ref;
            try {
                ref = storage.getReference(img.bucket_identifier);
            } catch (IllegalArgumentException exception) {
                Log.d("AlbumListener", exception.toString() + "\n path: " + img.bucket_identifier);
                ref = null;
            }

            // Get URI
            if (ref != null) {
                ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        img.setDownloadUri(quality, uri);
                        onUriFetched.callback(img);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Log.d("AlbumListener", exception.toString());
                        onUriFetched.callback(img);
                    }
                });
            }
        }
    }

    @Override
    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
    }

    @Override
    public void onChildRemoved(DataSnapshot dataSnapshot) {
    }

    @Override
    public void onChildMoved(DataSnapshot dataSnapshot, String s) {
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {
    }
}
