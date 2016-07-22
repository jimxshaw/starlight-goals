package me.jimmyshaw.starlightgoals;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import me.jimmyshaw.starlightgoals.adapters.AdapterGoals;
import me.jimmyshaw.starlightgoals.models.Goal;

public class ActivityMain extends AppCompatActivity {

    Realm realm;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.button_add_a_goal)
    Button buttonAdd;

    @BindView(R.id.recycler_view_goals)
    RecyclerView recyclerView;

    private View.OnClickListener addListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            showDialogAddAGoal();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        realm = Realm.getDefaultInstance();

        RealmResults<Goal> realmResults = realm.where(Goal.class).findAllAsync();

        buttonAdd.setOnClickListener(addListener);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(new AdapterGoals(this));

    }

    private void showDialogAddAGoal() {
        DialogAddAGoal dialog = new DialogAddAGoal();
        dialog.show(getSupportFragmentManager(), "Dialog Add a Goal");
    }
}
