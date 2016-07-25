package me.jimmyshaw.starlightgoals.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;
import io.realm.RealmResults;
import me.jimmyshaw.starlightgoals.R;
import me.jimmyshaw.starlightgoals.models.Goal;

public class AdapterGoals extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements SwipeListener {

    public static final String TAG = "Jim";

    Context context;

    private LayoutInflater inflater;

    private Realm realm;

    private RealmResults<Goal> realmResults;

    private AddListener addListener;
    private CompleteListener completeListener;

    // These arbitrary ints label the view types that are in our recycler view. We only have two
    // types in this case, either a row item or it's the footer. The view type ints will be used
    // in onCreateViewHolder.
    public static final int ROW_ITEM = 0;
    public static final int FOOTER = 1;

    public AdapterGoals(Context context, Realm realm, RealmResults<Goal> realmResults, AddListener addListener, CompleteListener completeListener) {
        this.context = context;
        this.addListener = addListener;
        this.completeListener = completeListener;
        inflater = LayoutInflater.from(context);
        this.realm = realm;

        update(realmResults);

    }

    public void update(RealmResults<Goal> realmResults) {
        // Our main activity will call this adapter's update method by passing in a new realm results
        // collection. This in turn will update each row bound by the view holder.
        this.realmResults = realmResults;
        notifyDataSetChanged();
    }

    @Override
    public void onSwipe(int position) {
        // Since deletion is a write command, we have to begin and commit a Realm transaction.
        // We add an if conditional to make sure we don't try to delete the footer.
        if (position < realmResults.size()) {
            realm.beginTransaction();
            realmResults.get(position).deleteFromRealm();
            realm.commitTransaction();
            // Refreshes the data set.
            notifyItemRemoved(position);
        }

        //resetFilterIfNoItems();
    }

    @Override
    public int getItemViewType(int position) {
        // What type of item view within our recycler view are we dealing with? Is it a row item or
        // is it the footer? This method determines that and the int will be passed into the
        // onCreateViewHolder method.
        if (realmResults == null || position < realmResults.size()) {
            return ROW_ITEM;
        }
        else {
            return FOOTER;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // This onCreateViewHolder method will be called repeatedly and we don't want to instantiate
        // the inflater every time. So make the inflater a class member variable.
        // We inflate either the footer holder or the goal holder depending on which viewType int
        // is passed in from getItemViewType.
        if (viewType == FOOTER) {
            View view = inflater.inflate(R.layout.recycler_view_footer, parent, false);

            return new FooterHolder(view, addListener);
        }
        else {
            View view = inflater.inflate(R.layout.recycler_view_row_goal, parent, false);

            return new GoalHolder(view, completeListener);
        }

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        // We pass into this method a general view holder. If the view holder is of type GoalHolder
        // then get a Goal object, by its position, from our collection and set the goalText property
        // to the goal text view.
        // If the view holder is another type, like FooterHolder, we do nothing because this would
        // mean our collection has no Goal objects to display.
        if (holder instanceof GoalHolder) {
            GoalHolder goalHolder = (GoalHolder) holder;
            Goal goal = realmResults.get(position);
            goalHolder.textViewGoalText.setText(goal.getGoal());
        }
    }

    @Override
    public int getItemCount() {
        // The actual item count is how many goals are in our realm results + 1. That plus 1 represents
        // our footer.
        // Obviously when our realm database has no goals displaying the footer wouldn't make sense.
        // So in that situation, we display the empty goals layout, which is our main activity layout.
        if (realmResults == null || realmResults.isEmpty()) {
            return 0;
        }
        else {
            return realmResults.size() + FOOTER;
        }
    }


    public static class GoalHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.text_view_goal_text)
        TextView textViewGoalText;

        @BindView(R.id.text_view_date_due)
        TextView textViewDateDue;

        private CompleteListener listener;

        public GoalHolder(View itemView, CompleteListener completeListener) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            listener = completeListener;

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            listener.onComplete(getAdapterPosition());
        }
    }

    public static class FooterHolder extends RecyclerView.ViewHolder {

        private AddListener listener;

        @BindView(R.id.button_footer)
        Button buttonFooter;

        @OnClick(R.id.button_footer)
        public void onClick() {
            listener.add();
        }

        public FooterHolder(View itemView, AddListener addListener) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            listener = addListener;
        }
    }
}
