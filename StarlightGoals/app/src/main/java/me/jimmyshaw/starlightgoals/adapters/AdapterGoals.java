package me.jimmyshaw.starlightgoals.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.jimmyshaw.starlightgoals.R;

public class AdapterGoals extends RecyclerView.Adapter<AdapterGoals.GoalHolder> {

    public static final String TAG = "Jim";

    Context context;

    private LayoutInflater inflater;

    private ArrayList<String> items = new ArrayList<>();

    public AdapterGoals(Context context) {
        this.context = context;

        inflater = LayoutInflater.from(context);

        items = generateValues();
    }

    public static ArrayList<String> generateValues() {
        ArrayList<String> dummyValues = new ArrayList<>();
        for (int i = 1; i < 101; i++) {
            dummyValues.add("Item " + i);
        }

        return dummyValues;

    }

    @Override
    public GoalHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // This onCreateViewHolder method will be called repeatedly and we don't want to instantiate
        // the inflater every time. So make the inflater a class member variable.
        View view = inflater.inflate(R.layout.recycler_view_row_goal, parent, false);

        Log.d(TAG, "onCreateViewHolder: ");

        GoalHolder goalHolder = new GoalHolder(view);

        return goalHolder;
    }

    @Override
    public void onBindViewHolder(GoalHolder holder, int position) {
        holder.textViewGoalText.setText(items.get(position));
        Log.d(TAG, "onBindViewHolder: " + position);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }


    public static class GoalHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.text_view_goal_text)
        TextView textViewGoalText;

        public GoalHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
