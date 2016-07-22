package me.jimmyshaw.starlightgoals.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

public class AdapterGoals extends RecyclerView.Adapter<AdapterGoals.GoalHolder> {


    @Override
    public GoalHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(GoalHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public static class GoalHolder extends RecyclerView.ViewHolder {

        public GoalHolder(View itemView) {
            super(itemView);
        }
    }
}
