package com.example.raffy.photoorganizer;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

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
