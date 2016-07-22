package me.jimmyshaw.starlightgoals;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import me.jimmyshaw.starlightgoals.models.Goal;

public class DialogAddAGoal extends DialogFragment {

    @BindView(R.id.image_button_close)
    ImageButton imageButtonClose;

    @BindView(R.id.button_add_goal)
    Button buttonAddGoal;

    @OnClick({R.id.image_button_close, R.id.button_add_goal})
    public void onButtonClick(View view) {
        int id = view.getId();

        switch (id) {
            case R.id.button_add_goal:
                performAddAction();
                break;
        }

        // Exits the entire Add a Goal dialog.
        dismiss();
    }

    @BindView(R.id.edit_text_add_a_goal)
    EditText editTextAddAGoal;

    @BindView(R.id.date_picker)
    DatePicker datePicker;


    public DialogAddAGoal() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_add_a_goal, container, false);

        ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


    }

    private void performAddAction() {
        // Get the value of the goal. Get the time of when it was added.
        String goalText = editTextAddAGoal.getText().toString();
        long dateAdded = System.currentTimeMillis();

        // To use Realm, we have to configure it and then add the configuration to a Realm instance.
        // Since we already configured Realm on start up in the Application configuration class we can
        // simply get a Realm instance without issue.
        Realm realm = Realm.getDefaultInstance();
        Goal goal = new Goal(dateAdded, 0, goalText, false);
        // Since copyToRealm is a write instruction, it must be used with a transaction.
        realm.beginTransaction();
        realm.copyToRealm(goal);
        realm.commitTransaction();
        realm.close();

    }
}
