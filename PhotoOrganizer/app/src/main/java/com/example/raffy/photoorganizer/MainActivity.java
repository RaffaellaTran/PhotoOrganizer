package com.example.raffy.photoorganizer;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessaging;

public class MainActivity extends AppCompatActivity {

    private static final int MY_CAMERA_REQUEST_CODE = 100;
    GridView gridView;

    static final String[] data = new String[]{
            "Gallery",
            "Take photo",
            "Group management",
            "Settings"
    };
    Intent intent;

    private FirebaseAuth auth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Check permissions
        if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[] {android.Manifest.permission.CAMERA}, MY_CAMERA_REQUEST_CODE);
        }

        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();

        // Set push-notification related settings
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create channel to show notifications.
            String channelId  = getString(R.string.default_notification_channel_id);
            String channelName = getString(R.string.default_notification_channel_name);
            NotificationManager notificationManager =
                    getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(new NotificationChannel(channelId,
                    channelName, NotificationManager.IMPORTANCE_LOW));
        }
        // Start listening to push notifications sent to this uid
        FirebaseMessaging.getInstance().subscribeToTopic(FirebaseAuth.getInstance().getUid());
        
        gridView = (GridView) findViewById(R.id.gridView);
        CustomGridAdapter customGridAdapter = new CustomGridAdapter(getApplicationContext(), data);
        gridView.setAdapter(customGridAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // set an Intent to Another Activity

                switch (data[position]) {
                    case "Gallery":
                        // case data.equals(""):
                        intent = new Intent(MainActivity.this, GalleryActivity.class);
                        startActivity(intent); // start Intent
                        break;
                    case "Take photo":
                        intent = new Intent(MainActivity.this, CameraActivity.class);
                        startActivity(intent); // start Intent
                        break;
                    case "Group management":
                        intent = new Intent(MainActivity.this, GroupManagementActivity.class);
                        startActivity(intent); // start Intent
                        break;
                    case "Settings":
                        intent = new Intent(MainActivity.this, SettingsActivity.class);
                        startActivity(intent); // start Intent
                        break;
                }

            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_CAMERA_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Camera permission granted", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Camera permission denied", Toast.LENGTH_LONG).show();
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CAMERA}, MY_CAMERA_REQUEST_CODE);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.search:
                //your code here

                Intent intent = new Intent(MainActivity.this, LoginActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                finish();
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                auth.signOut();
                startActivity(intent);
                finish();

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
