package me.jimmyshaw.starlightgoals.adapters;

import android.graphics.Canvas;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;

public class SimpleTouchCallback extends ItemTouchHelper.Callback {

    public static final String TAG = "Jim";

    private SwipeListener swipeListener;

    public SimpleTouchCallback(SwipeListener swipeListener) {
        this.swipeListener = swipeListener;
    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        // dragFlags, swipeFlags
        // By assigning ItemTouchHelper.END, if the app uses a language that supports right-to-left
        // then the end would be right. If the app uses a right-to-left language then end is left.
        return makeMovementFlags(0, ItemTouchHelper.END);
    }

    // We don't want to drag to delete but use swipe to delete instead so we return false and true
    // for those associated methods.
    @Override
    public boolean isLongPressDragEnabled() {
        return false;
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        return true;
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        // This method has to communicate with the recycler view adapter in order to delete the row
        // item that has been swiped.
        // This if conditional is needed so that only row items can be swiped away. Otherwise, the footer
        // or the no items layout can be swiped away. But this isn't enough as we have to override
        // two methods onChildDraw and onChildDrawOver as their default behavior is what governs
        // the swiping in the x direction or y direction.
        if (viewHolder instanceof AdapterGoals.GoalHolder) {
            swipeListener.onSwipe(viewHolder.getLayoutPosition());
        }
    }

    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        Log.d(TAG, "onChildDraw -> dX: " + dX + " dY: " + dY);
        if (viewHolder instanceof AdapterGoals.GoalHolder) {
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    }

    @Override
    public void onChildDrawOver(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        Log.d(TAG, "onChildDrawOver -> dX: " + dX + " dY: " + dY);
        if (viewHolder instanceof AdapterGoals.GoalHolder) {
            super.onChildDrawOver(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    }
}
