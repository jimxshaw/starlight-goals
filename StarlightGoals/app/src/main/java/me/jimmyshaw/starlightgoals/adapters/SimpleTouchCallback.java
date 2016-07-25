package me.jimmyshaw.starlightgoals.adapters;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

public class SimpleTouchCallback extends ItemTouchHelper.Callback {

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
        swipeListener.onSwipe(viewHolder.getLayoutPosition());
    }
}
