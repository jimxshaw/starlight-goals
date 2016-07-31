package me.jimmyshaw.starlightgoals;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import io.realm.Sort;
import me.jimmyshaw.starlightgoals.adapters.AdapterGoals;
import me.jimmyshaw.starlightgoals.adapters.AddListener;
import me.jimmyshaw.starlightgoals.adapters.CompleteListener;
import me.jimmyshaw.starlightgoals.adapters.DetailListener;
import me.jimmyshaw.starlightgoals.adapters.Filter;
import me.jimmyshaw.starlightgoals.adapters.IncompleteListener;
import me.jimmyshaw.starlightgoals.adapters.ResetListener;
import me.jimmyshaw.starlightgoals.adapters.SimpleTouchCallback;
import me.jimmyshaw.starlightgoals.models.Goal;
import me.jimmyshaw.starlightgoals.services.ServiceNotification;
import me.jimmyshaw.starlightgoals.utilities.CustomRecyclerViewDivider;
import me.jimmyshaw.starlightgoals.widgets.CustomRecyclerView;

public class ActivityMain extends AppCompatActivity {

    public static final String TAG = "Jim";
    public static final String ARG_POSITION = "POSITION";

    Realm realm;

    AdapterGoals adapterGoals;

    RealmResults<Goal> realmResults;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.button_add_a_goal)
    Button buttonAdd;

    @BindView(R.id.recycler_view_goals)
    CustomRecyclerView recyclerView;

    @BindView(R.id.empty_goals)
    View viewEmptyGoals;

    // This listener is specific to the Add button on our main activity.
    private View.OnClickListener buttonAddAGoalListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            showDialogAddAGoal();
        }
    };

    // This listener interface implementation is specific to the Add button in our recycler
    // view's footer view holder. We pass this listener as an argument in our adapter's
    // constructor whenever we instantiate it.
    private AddListener addListener = new AddListener() {
        @Override
        public void add() {
            showDialogAddAGoal();
        }
    };

    private DetailListener detailListener = new DetailListener() {
        @Override
        public void onClick(int position) {
            // Check to see if the goal at the clicked position is completed or not. Only show the
            // dialog to mark a goal as completed if the goal is incomplete. If the goal is already
            // complete then do nothing because there wouldn't be any point to show that dialog.
            boolean isClickedGoalAlreadyComplete = realmResults.get(position).isCompleted();
            if (!isClickedGoalAlreadyComplete) {
                showDialogCompleteThisGoal(position);
            }
        }
    };

    private CompleteListener completeListener = new CompleteListener() {
        @Override
        public void onComplete(int position) {
            adapterGoals.completeThisGoal(position);
        }
    };

    // We'd like to give the user the change to mark a goal as incomplete after it has been marked
    // as completed. Instead of creating another detail layout, we'd like to feature the recycler
    // view's long press functionality. When the user long presses on a completed goal, it will be
    // marked as incomplete.
    private IncompleteListener incompleteListener = new IncompleteListener() {
        @Override
        public void onIncomplete(int position) {
            adapterGoals.markAsIncomplete(position);
        }
    };

    private ResetListener resetListener = new ResetListener() {
        @Override
        public void onReset() {
            // When do we reset the app? We reset it when there's no data in our realm database and
            // AdapterGoals is the class that manages the data. So we have to pass this listener
            // into the adapter's constructor. Within adapter's onSwipe, we check our database conditions
            // and call this listener's onReset if the conditions are met.
            AppStarlightGoals.saveToSharedPreferences(ActivityMain.this, Filter.OFF);
            loadRealmResults(Filter.OFF);
        }
    };

    private RealmChangeListener realmChangeListener = new RealmChangeListener() {
        @Override
        public void onChange(Object element) {
            Log.d(TAG, "onChange: ");
            adapterGoals.update(realmResults);
        }
    };

    private void showDialogAddAGoal() {
        DialogAddAGoal dialog = new DialogAddAGoal();
        dialog.show(getSupportFragmentManager(), "Dialog Add a Goal");
    }

    private void showDialogCompleteThisGoal(int position) {
        // This method is called when the user clicks on a particular row item and wants to mark it
        // complete in the DialogCompleteThisGoal fragment. How does the dialog know which row
        // item to mark as complete? That's why we have to pass in to this method the row item's
        // position as a bundle argument. Our dialog fragment will get out this bundle argument and
        // process it accordingly.
        DialogCompleteThisGoal dialog = new DialogCompleteThisGoal();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_POSITION, position);
        dialog.setArguments(bundle);
        // We pass in the CompleteListener and its implemented onComplete method to DialogDetail so
        // that when the user clicks the Mark completed button, the row item at its position can
        // be marked as complete.
        dialog.setCompleteListener(completeListener);
        dialog.show(getSupportFragmentManager(), "Dialog Complete This Goal");
    }


    private void loadRealmResults(int filterOption) {
        switch (filterOption) {
            case Filter.OFF:
                realmResults = realm.where(Goal.class).findAllAsync();
                break;
            case Filter.MOST_TIME_REMAINING:
                realmResults = realm.where(Goal.class).findAllSortedAsync("dateDue", Sort.DESCENDING);
                break;
            case Filter.LEAST_TIME_REMAINING:
                realmResults = realm.where(Goal.class).findAllSortedAsync("dateDue");
                break;
            case Filter.COMPLETED:
                realmResults = realm.where(Goal.class).equalTo("completed", true).findAllAsync();
                break;
            case Filter.INCOMPLETE:
                realmResults = realm.where(Goal.class).equalTo("completed", false).findAllAsync();
                break;
        }
        realmResults.addChangeListener(realmChangeListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        realm = Realm.getDefaultInstance();

        int filterOption = AppStarlightGoals.loadFromSharedPreferences(this);
        loadRealmResults(filterOption);

        realmResults = realm.where(Goal.class).findAllAsync();

        buttonAdd.setOnClickListener(buttonAddAGoalListener);

        recyclerView.addItemDecoration(new CustomRecyclerViewDivider(this, LinearLayoutManager.VERTICAL));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.hideIfEmpty(toolbar);
        recyclerView.showIfEmpty(viewEmptyGoals);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapterGoals = new AdapterGoals(this,
                realm,
                realmResults,
                addListener,
                detailListener,
                resetListener,
                incompleteListener);
        // To have animations with our row items, we set this field to true. The stable id is what's
        // returned in the adapter's getItemId method and that id will be used with the recycler
        // view's setItemAnimator. We're using the default animation but we could pass in other
        // animators if we wanted.
        adapterGoals.setHasStableIds(true);
        recyclerView.setAdapter(adapterGoals);

        // Our adapter implements SwipeListener and so we simply pass that into the constructor.
        SimpleTouchCallback simpleTouchCallback = new SimpleTouchCallback(adapterGoals);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        // The vision for using an alarm manager is that we'll be combining it with a service so that
        // whenever a goal is 90%, 95% or whatever other % finished approaching its due date time then
        // we'll notify the user. We want our alarm service to check if any goals are approaching their
        // due date time once every hour.
        // The reason we're putting our intent inside a pending intent is because we want the alarm
        // service to be checking due dates regardless if our app is running. We'd even like it to check
        // when the actual phone is asleep. A service inside a pending intent will run no matter if its
        // hosting app process is destroyed.
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent intent = new Intent(this, ServiceNotification.class);
        // The requestCode int, which we arbitrarily put as 100, is an int used to differentiate
        // this pending intent from other pending intents. The final parameter, FLAG_UPDATE_CURRENT
        // means that if the described PendingIntent already exists, then keep it but replace its
        // extra data with what is in this new Intent.
        PendingIntent pendingIntent = PendingIntent.getService(this, 101, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        // ELAPSED_REALTIME_WAKEUP will use real time plus will wake up the phone to run whatever is in
        // the pending intent. Second parameter is the trigger time, after wakeup how long before the
        // pending intent triggers. Third parameter is the interval time, how often do we want the
        // pending intent to fire. Note that the interval time is recommended to be an hour or more.
        // If not then the device's battery will drain quickly.
        alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, 1000, 5000, pendingIntent);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        // The menu will only appear only if this method returns true, otherwise
        // no menu will be shown.
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        // This method's boolean determines who handles the menu item's action. True means our code,
        // the developer, handles the action. False means Android handles the action.
        // We assume the item action was handled successfully but if not then the default switch
        // case will return false.
        // The toolbar's title changes to the filter option selected as a convenience to the user.
        // If the filter is none then the default title of our app's name is shown.
        boolean isHandled = true;
        int filterOption = Filter.OFF;

        switch (menuItem.getItemId()) {
            case R.id.action_add:
                showDialogAddAGoal();
                break;
            case R.id.action_filter_date_desc:
                filterOption = Filter.MOST_TIME_REMAINING;
                toolbar.setTitle(menuItem.getTitle());
                break;
            case R.id.action_filter_date_asc:
                filterOption = Filter.LEAST_TIME_REMAINING;
                toolbar.setTitle(menuItem.getTitle());
                break;
            case R.id.action_filter_completed:
                filterOption = Filter.COMPLETED;
                toolbar.setTitle(menuItem.getTitle());
                break;
            case R.id.action_filter_incomplete:
                filterOption = Filter.INCOMPLETE;
                toolbar.setTitle(menuItem.getTitle());
                break;
            case R.id.action_filter_off:
                filterOption = Filter.OFF;
                toolbar.setTitle(R.string.app_name);
                break;
            case R.id.action_filter_symbol:
                // Android treats clicking the filter symbol as an actual action before the sub-menus
                // containing the filter options appear. We have to exit the method with a return statement
                // immediately to signify that clicking the filter symbol itself does nothing. After
                // clicking the filter symbol, which does nothing, the sub-menu with its items will show up.
                return isHandled;
            default:
                isHandled = false;
                break;
        }

        AppStarlightGoals.saveToSharedPreferences(this, filterOption);
        loadRealmResults(filterOption);
        return isHandled;
    }

    @Override
    protected void onStart() {
        super.onStart();
        realmResults.addChangeListener(realmChangeListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        realmResults.addChangeListener(realmChangeListener);
    }
}
