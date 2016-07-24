package me.jimmyshaw.starlightgoals.utilities;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import me.jimmyshaw.starlightgoals.R;
import me.jimmyshaw.starlightgoals.adapters.AdapterGoals;

public class CustomRecyclerViewDivider extends RecyclerView.ItemDecoration {

    // Our actual divider is located in a drawable xml file.
    // It's a drawable shape with a width and height.
    private Drawable divider;
    // The orientation matters because our project uses a vertical LinearLayoutManager only. If
    // we were to use other orientations like horizontal LinearLayoutManager, for example, then our
    // present code won't work because that orientation needs different calculations.
    private int orientation;

    public CustomRecyclerViewDivider(Context context, int orientation) {
        divider = ContextCompat.getDrawable(context, R.drawable.recycler_view_row_divider);

        // The orientation parameter is used to check and make sure our recycler view is running
        // in the vertical orientation when we draw the divider.
        if (orientation != LinearLayoutManager.VERTICAL) {
            throw new IllegalArgumentException("This item decoration can only be used with a RecyclerView " +
                    "that has a LinearLayoutManager with its orientation set to vertical.");
        }

        this.orientation = orientation;
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        if (orientation == LinearLayoutManager.VERTICAL) {
            drawHorizontalDivider(c, parent, state);
        }
    }

    private void drawHorizontalDivider(Canvas canvas, RecyclerView parent, RecyclerView.State state) {
        // Four boundary values are needed to draw our divider.
        int left, top, right, bottom;

        // Force the divider to respect each row item's padding.
        left = parent.getPaddingLeft();
        right = parent.getWidth() - parent.getPaddingRight();

        int count = parent.getChildCount();
        for (int i = 0; i < count; i++) {
            // If our recycler view item type is the footer then we don't draw the divider.
            if (AdapterGoals.FOOTER != parent.getAdapter().getItemViewType(i)) {
                View currentChild = parent.getChildAt(i);
                // Force the divider to respect the child view's margins by using LayoutParams.
                RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) currentChild.getLayoutParams();
                top = currentChild.getTop() - params.topMargin;
                bottom = top + divider.getIntrinsicHeight();
                // After determining the boundaries values, we set the bounds of the divider
                // and then draw it.
                divider.setBounds(left, top, right, bottom);
                divider.draw(canvas);
            }
        }
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        // Modify the boundary values to change spacing between row items.
        if (orientation == LinearLayoutManager.VERTICAL) {
            // left, top, right, bottom
            outRect.set(0, 0, 0, divider.getIntrinsicHeight());
        }
    }
}
