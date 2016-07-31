package me.jimmyshaw.starlightgoals.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import me.jimmyshaw.starlightgoals.utilities.Util;

// Our app's Notification Service will be disabled whenever the user's device restarts. The service
// needs to be triggered again in the background, without ever forcing the user to re-open our app.
// This broadcast receiver listens for device restarts and upon bootup will trigger the service.
public class ReceiverBootup extends BroadcastReceiver {

    public static final String TAG = "Jim";

    public ReceiverBootup() {
        Log.d(TAG, "ReceiverBootup: ");
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive: ");
        Util.scheduleAlarm(context);
    }
}
