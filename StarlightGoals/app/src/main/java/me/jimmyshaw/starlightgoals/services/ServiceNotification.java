package me.jimmyshaw.starlightgoals.services;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 */
public class ServiceNotification extends IntentService {

    public static final String TAG = "Jim";

    public ServiceNotification() {
        super("ServiceNotification");
        Log.d(TAG, "ServiceNotification");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "onHandleIntent");
    }


}
