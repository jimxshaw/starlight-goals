package me.jimmyshaw.starlightgoals;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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
import me.jimmyshaw.starlightgoals.adapters.SimpleTouchCallback;
import me.jimmyshaw.starlightgoals.models.Goal;
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
            showDialogCompleteThisGoal(position);
        }
    };

    private CompleteListener completeListener = new CompleteListener() {
        @Override
        public void onComplete(int position) {
            adapterGoals.completeThisGoal(position);
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
        recyclerView.hideIfEmpty(toolbar);
        recyclerView.showIfEmpty(viewEmptyGoals);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapterGoals = new AdapterGoals(this, realm, realmResults, addListener, detailListener);
        recyclerView.setAdapter(adapterGoals);

        // Our adapter implements SwipeListener and so we simply pass that into the constructor.
        SimpleTouchCallback simpleTouchCallback = new SimpleTouchCallback(adapterGoals);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

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
