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
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;
import io.realm.RealmResults;
import me.jimmyshaw.starlightgoals.AppStarlightGoals;
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
    private ResetListener resetListener;
    private IncompleteListener incompleteListener;

    private int filterOption;

    // These arbitrary ints label the view types that are in our recycler view. We only have two
    // types in this case, either a row item or it's the footer. The view type ints will be used
    // in onCreateViewHolder.
    public static final int ROW_ITEM = 0;
    public static final int NO_ITEMS = 1;
    public static final int FOOTER = 2;

    // These two int constants will be used with the getItemsCount method.
    public static final int COUNT_NO_ITEMS = 1;
    public static final int COUNT_FOOTER = 1;

    public AdapterGoals(Context context,
                        Realm realm,
                        RealmResults<Goal> realmResults,
                        AddListener addListener,
                        DetailListener detailListener,
                        ResetListener resetListener,
                        IncompleteListener incompleteListener) {
        // Since the update method uses the context, the context must be assigned prior to calling the
        // update method.
        this.context = context;
        inflater = LayoutInflater.from(context);
        update(realmResults);
        this.realm = realm;
        this.addListener = addListener;
        this.detailListener = detailListener;
        this.resetListener = resetListener;
        this.incompleteListener = incompleteListener;
    }

    public void update(RealmResults<Goal> realmResults) {
        // Our main activity will call this adapter's update method by passing in a new realm results
        // collection. This in turn will update each row bound by the view holder.
        this.realmResults = realmResults;
        filterOption = AppStarlightGoals.loadFromSharedPreferences(context);
        notifyDataSetChanged();
    }

    private void resetFilterIfNoItems() {
        if (realmResults.isEmpty() && (filterOption == Filter.COMPLETED || filterOption == Filter.INCOMPLETE)) {
            resetListener.onReset();
        }
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

        resetFilterIfNoItems();
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

    public void markAsIncomplete(int position) {
        if (position < realmResults.size()) {
            realm.beginTransaction();
            realmResults.get(position).setCompleted(false);
            realm.commitTransaction();
            notifyItemChanged(position);
        }
    }

    @Override
    public long getItemId(int position) {
        // This method is used capture a unique id of a row item so we can use it to apply animations to
        // it. Since more than one row item cannot be add at the same time, we'll use the dateAdded field
        // of each Goal object as the unique identifier.
        if (position < realmResults.size()) {
            return realmResults.get(position).getDateAdded();
        }
        return RecyclerView.NO_ID;
    }

    @Override
    public int getItemViewType(int position) {
        // We add a conditional to capture the null scenario to prevent null pointer exceptions.
        // Since collections always start at position 0, if the position is less than the collection size
        // then it will be a goal, otherwise it's the footer.
        if (!realmResults.isEmpty()) {
            if (position < realmResults.size()) {
                return ROW_ITEM;
            }
            else {
                return FOOTER;
            }
        }
        else {
            if (filterOption == Filter.COMPLETED || filterOption == Filter.INCOMPLETE) {
                if (position == 0) {
                    return NO_ITEMS;
                }
                else {
                    return FOOTER;
                }
            }
            else {
                return ROW_ITEM;
            }
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
        else if (viewType == NO_ITEMS) {
            View view = inflater.inflate(R.layout.recycler_view_no_items, parent, false);

            return new NoItemsHolders(view);
        }
        else {
            View view = inflater.inflate(R.layout.recycler_view_row_goal, parent, false);

            return new GoalHolder(view, detailListener, incompleteListener);
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
        // The results collection does not encompass the footer. We must add 1 to the collection size
        // for the recycler view to show goals and the footer. Using only the collection's size,
        // we'll just see the goals.
        // Only after querying every single filter option and the results still being 0 then we'll
        // shown the blank main activity screen.
        // If the user applied a complete or incomplete filter that's stored in shared preferences
        // and that filter produces no items then we'll show the no items layout with the footer
        // added behind it.
        if (!realmResults.isEmpty()) {
            // Our realm results have data.
            return realmResults.size() + COUNT_FOOTER;
        }
        else {
            // Our realm results are null.
            if (filterOption == Filter.MOST_TIME_REMAINING ||
                    filterOption == Filter.LEAST_TIME_REMAINING ||
                    filterOption == Filter.OFF) {
                // We return 0 since our realm results are empty and we searched every single filter.
                return 0;
            }
            else {
                return COUNT_NO_ITEMS + COUNT_FOOTER;
            }
        }
    }


    public static class GoalHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener, View.OnLongClickListener {

        @BindView(R.id.text_view_goal_text)
        TextView textViewGoalText;

        @BindView(R.id.text_view_date_due)
        TextView textViewDateDue;

        private Context context;

        private View itemView;

        private DetailListener detailListener;
        private IncompleteListener incompleteListener;

        public GoalHolder(View itemView, DetailListener detailListener, IncompleteListener incompleteListener) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            context = itemView.getContext();
            this.itemView = itemView;
            this.detailListener = detailListener;
            this.incompleteListener = incompleteListener;

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);

            // Set a different font for our row item text widgets.
            AppStarlightGoals.setWidgetTypeface(context, textViewGoalText, textViewDateDue);
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
            detailListener.onClick(getAdapterPosition());
        }

        @Override
        public boolean onLongClick(View view) {
            incompleteListener.onIncomplete(getAdapterPosition());
            return true;
        }
    }


    public static class NoItemsHolders extends RecyclerView.ViewHolder {

        public NoItemsHolders(View itemView) {
            super(itemView);
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
