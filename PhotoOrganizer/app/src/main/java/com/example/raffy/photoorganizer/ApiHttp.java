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
    private String successMessage;
    private String failureMessage;

    ApiHttp(Activity context, ProgressDialog progress, String successMessage, String failureMessage) {
        this.context = new WeakReference<>(context);
        this.progress = progress;
        this.successMessage = successMessage;
        this.failureMessage = failureMessage;
    }

    @Override
    protected String doInBackground(Request... requests) {
        OkHttpClient client = new OkHttpClient();
        //noinspection LoopStatementThatDoesntLoop
        for (Request request : requests) {
            try {
                Response response = client.newCall(request).execute();
                if (response.code() == 200) {
                    Log.i("ApiHttp: ", response.toString());
                    return successMessage;
                } else {
                    Log.e("ApiHttp: ", response.toString());
                    return failureMessage + ": " + response.toString();
                }
            } catch (IOException e) {
                Log.e("ApiHttp: ", e.getMessage());
                return failureMessage + ": " + e.getMessage();
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(String message) {
        super.onPostExecute(message);
        if (message != null) {
            Toast.makeText(context.get(), message, Toast.LENGTH_LONG).show();
        }
        progress.dismiss();
        context.get().finish();
    }

    static ProgressDialog getProgressDialog(Activity context) {
        ProgressDialog progress = new ProgressDialog(context);
        progress.setCancelable(false);
        progress.show();
        return progress;
    }

}
