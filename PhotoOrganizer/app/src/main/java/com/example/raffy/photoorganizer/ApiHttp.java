package com.example.raffy.photoorganizer;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.lang.ref.WeakReference;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ApiHttp extends AsyncTask<Request, Void, String> {

    private WeakReference<Activity> context;
    private ProgressDialog progress;

    ApiHttp(Activity context, ProgressDialog progress) {
        this.context = new WeakReference<>(context);
        this.progress = progress;
    }

    @Override
    protected String doInBackground(Request... requests) {
        OkHttpClient client = new OkHttpClient();
        for (Request request : requests) {
            try {
                Response response = client.newCall(request).execute();
                return "Success! " + response.toString();
            } catch (IOException e) {
                return "Network error: " + e.getMessage();
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(String message) {
        super.onPostExecute(message);
        if (message != null) {
            Log.i("ApiHttp: ", message);
            Toast.makeText(context.get(), "Success!!", Toast.LENGTH_LONG).show();
        }
        progress.dismiss();
        context.get().finish();
    }

    public static ProgressDialog getProgressDialog(Activity context) {
        ProgressDialog progress = new ProgressDialog(context);
        progress.setCancelable(false);
        progress.show();
        return progress;
    }

}
