package me.jimmyshaw.starlightgoals.services;

import android.app.IntentService;
import android.app.Notification;
import android.content.Intent;
import android.util.Log;

import br.com.goncalves.pugnotification.notification.PugNotification;
import io.realm.Realm;
import io.realm.RealmResults;
import me.jimmyshaw.starlightgoals.ActivityMain;
import me.jimmyshaw.starlightgoals.R;
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

    private void issueNotification(Goal goal) {
        String message = getString(R.string.notification_message_small_device)
                + " \"" + goal.getGoal() + "\"";

        PugNotification.with(this)
                .load()
                .title(R.string.app_name)
                .message(message)
                .bigTextStyle(message)
                .smallIcon(R.drawable.ic_star)
                .largeIcon(R.drawable.ic_polaris)
                .flags(Notification.DEFAULT_ALL) // The defaults are lights, sound and vibrate.
                .autoCancel(true) // Clears the message if the user clicks on it.
                .click(ActivityMain.class) // Clicking on the message will take the user to this app's main activity.
                .simple()
                .build();
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
                if (isNotificationNeeded(currentGoal.getDateAdded(), currentGoal.getDateDue())) {
                    issueNotification(currentGoal);
                }
            }
        }
        finally {
            if (realm != null) {
                realm.close();
            }
        }
    }


}
