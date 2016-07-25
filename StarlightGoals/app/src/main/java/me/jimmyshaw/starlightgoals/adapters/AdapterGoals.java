package me.jimmyshaw.starlightgoals.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
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
import me.jimmyshaw.starlightgoals.utilities.Util;

public class AdapterGoals extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements SwipeListener {

    public static final String TAG = "Jim";

    Context context;

    private LayoutInflater inflater;

    private Realm realm;

    private RealmResults<Goal> realmResults;

    private AddListener addListener;
    private DetailListener detailListener;

    // These arbitrary ints label the view types that are in our recycler view. We only have two
    // types in this case, either a row item or it's the footer. The view type ints will be used
    // in onCreateViewHolder.
    public static final int ROW_ITEM = 0;
    public static final int FOOTER = 1;

    public AdapterGoals(Context context, Realm realm, RealmResults<Goal> realmResults, AddListener addListener, DetailListener detailListener) {
        this.context = context;
        this.addListener = addListener;
        this.detailListener = detailListener;
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

    public void completeThisGoal(int position) {
        // We add an if conditional to make sure don't mark the footer as complete.
        if (position < realmResults.size()) {
            realm.beginTransaction();
            realmResults.get(position).setCompleted(true);
            realm.commitTransaction();
            // Refreshes the data set.
            notifyItemChanged(position);
        }
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

            return new GoalHolder(view, detailListener);
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

            goalHolder.setGoalText(goal.getGoal());
            goalHolder.setDateDue(goal.getDateDue());
            goalHolder.setBackground(goal.isCompleted());
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

        private Context context;

        private View itemView;

        private DetailListener listener;

        public GoalHolder(View itemView, DetailListener detailListener) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            context = itemView.getContext();

            this.itemView = itemView;

            listener = detailListener;

            itemView.setOnClickListener(this);
        }

        public void setGoalText(String goalText) {
            textViewGoalText.setText(goalText);
        }

        public void setDateDue(long dateDue) {
            // The third parameter of getRelativeTimeSpanString is called minResolution, which is the
            // minimum about of time period that we'd like notify the user. Day is a perfect time
            // period. When a goal is due today, the text will display today.
            textViewDateDue.setText(DateUtils.getRelativeTimeSpanString(
                    dateDue,
                    System.currentTimeMillis(),
                    DateUtils.DAY_IN_MILLIS,
                    DateUtils.FORMAT_ABBREV_ALL));
        }

        public void setBackground(Boolean isCompleted) {
            // Depending on whether or not the row item is completed, a different drawable will be
            // returned. The returned drawable has to be set to a view and that view is the row item's
            // view that's passed in to DropHolder's constructor every time the constructor is called.
            Drawable drawable;
            if (isCompleted) {
                drawable = ContextCompat.getDrawable(context, R.color.background_recycler_view_row_goal_completed);
            }
            else {
                drawable = ContextCompat.getDrawable(context, R.drawable.background_recycler_view_row_goal);
            }
            Util.setBackground(itemView, drawable);
        }

        @Override
        public void onClick(View view) {
            listener.onClick(getAdapterPosition());
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
