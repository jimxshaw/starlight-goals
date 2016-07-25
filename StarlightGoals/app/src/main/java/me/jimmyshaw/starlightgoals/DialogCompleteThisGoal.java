package me.jimmyshaw.starlightgoals;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DialogCompleteThisGoal extends DialogFragment {

    @BindView(R.id.image_button_close)
    ImageButton buttonClose;

    @BindView(R.id.button_complete_this_goal)
    Button buttonComplete;

    @OnClick({R.id.image_button_close, R.id.button_complete_this_goal})
    public void onClick(View view) {
        int id = view.getId();

        switch (id) {
            case R.id.button_complete_this_goal:
                //TODO: Handle action to mark the row item as complete.
                break;
        }

        dismiss();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_complete_this_goal, container, false);

        ButterKnife.bind(this, view);

        return view;
    }
}
