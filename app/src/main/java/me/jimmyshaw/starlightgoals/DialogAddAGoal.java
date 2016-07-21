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
        String goal = editTextAddAGoal.getText().toString();

        long currentTime = System.currentTimeMillis();
    }
}
