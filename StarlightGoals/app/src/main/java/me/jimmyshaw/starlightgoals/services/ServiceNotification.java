package me.jimmyshaw.starlightgoals.services;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import io.realm.Realm;
import io.realm.RealmResults;
import me.jimmyshaw.starlightgoals.models.Goal;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 */
public class ServiceNotification extends IntentService {

    public static final String TAG = "Jim";

    public ServiceNotification() {
        super("ServiceNotification");
        Log.d(TAG, "ServiceNotification:");
    }

    private boolean isNotificationNeeded(long dateAdded, long dateDue) {
        long currentTime = System.currentTimeMillis();

        if (currentTime > dateDue) {
            // If we're already passed a goal's due date then it doesn't make sense to send notifications.
            return false;
        }
        else {
            // Notify the user when a certain percentage of the due date time has passed.
            long ninetyPercentDifference = (long) (0.90 * (dateDue - dateAdded));

            // If the current time has passed the time when the goal was added plus the specified
            // percentage then return true, otherwise return false.
            return (currentTime > (dateAdded + ninetyPercentDifference)) ? true : false;
        }
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "onHandleIntent:");

        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            // Query realm and find all our incomplete goals. We don't need to use the findAllAsync
            // method because we're already in a background thread with this service.
            RealmResults<Goal> realmResults = realm.where(Goal.class).equalTo("completed", false).findAll();

            for (Goal currentGoal : realmResults) {

            }
        }
        finally {
            if (realm != null) {
                realm.close();
            }
        }
    }


}
