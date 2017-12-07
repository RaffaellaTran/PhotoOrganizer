package com.example.raffy.photoorganizer;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Adapter;
import android.widget.BaseAdapter;
import android.widget.Toast;

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
    Context context;
    SettingsHelper preferences;

    public AlbumListener(AlbumEventListener onNewImage, AlbumEventListener onUriFetched, Context context) {
        this.context = context;
        this.onNewImage = onNewImage;
        this.onUriFetched = onUriFetched;
        this.preferences = new SettingsHelper(this.context);
    }

    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
        final GalleryImage img = dataSnapshot.getValue(GalleryImage.class);
        //album.images.add(img);
        onNewImage.callback(img);


        // Get image storage reference
        FirebaseStorage storage = this.preferences.getFirebaseStorage(this.preferences.getImageQuality());
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
                    img.downloadUri = uri;
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
        Toast.makeText(context, "Firebase error occurred!", Toast.LENGTH_SHORT).show();
    }
}
