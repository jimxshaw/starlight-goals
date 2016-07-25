package me.jimmyshaw.starlightgoals;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import me.jimmyshaw.starlightgoals.adapters.AdapterGoals;
import me.jimmyshaw.starlightgoals.adapters.AddListener;
import me.jimmyshaw.starlightgoals.adapters.CompleteListener;
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

    private CompleteListener completeListener = new CompleteListener() {
        @Override
        public void onComplete(int position) {
            showDialogCompleteThisGoal(position);
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
        // complete in the dialog complete this goal fragment. How does the dialog know which row
        // item to mark as complete? That's why we have to pass in to this method the row item's
        // position as a bundle argument. Our dialog fragment will get out this bundle argument and
        // process it accordingly.
        DialogCompleteThisGoal dialog = new DialogCompleteThisGoal();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_POSITION, position);
        dialog.setArguments(bundle);
        dialog.show(getSupportFragmentManager(), "Dialog Complete This Goal");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        realm = Realm.getDefaultInstance();

        realmResults = realm.where(Goal.class).findAllAsync();

        buttonAdd.setOnClickListener(buttonAddAGoalListener);

        recyclerView.addItemDecoration(new CustomRecyclerViewDivider(this, LinearLayoutManager.VERTICAL));
        recyclerView.hideIfEmpty(toolbar);
        recyclerView.showIfEmpty(viewEmptyGoals);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapterGoals = new AdapterGoals(this, realm, realmResults, addListener, completeListener);
        recyclerView.setAdapter(adapterGoals);

        // Our adapter implements SwipeListener and so we simply pass that into the constructor.
        SimpleTouchCallback simpleTouchCallback = new SimpleTouchCallback(adapterGoals);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

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
