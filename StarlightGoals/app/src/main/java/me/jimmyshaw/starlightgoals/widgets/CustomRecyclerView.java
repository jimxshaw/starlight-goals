package me.jimmyshaw.starlightgoals.widgets;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

// This is a custom recycler view class that behaves differently from the normal recycler view.
public class CustomRecyclerView extends RecyclerView {

    // Using empty list will allow us to initialize a list with zero items. This is the only way to initialize
    // an empty list and to avoid null pointer exceptions. Also, a list cannot be initialized to a
    // list because it's an interface and list itself is not an implementation of list.
    private List<View> viewsToShowWhenRecyclerViewIsEmpty = Collections.emptyList();
    private List<View> viewsToShowWhenRecyclerViewHasData = Collections.emptyList();

    // Our app's main activity includes a recycler view and a tool bar. The vision is that the
    // recycler view and tool bar will only appear when the recycler view has data to display.
    // If there's no data then the main UI will only show our app's symbol and the Add a Star button.
    // To do this, we use the abstract class called AdapterDataObserver with methods that will
    // each check whether or not the recycler view contains data. All the override methods in the
    // AdapterDataObserver class have to be included because we have to check if the recycler view
    // has data under all circumstances.
    private AdapterDataObserver adapterDataObserver = new AdapterDataObserver() {
        @Override
        public void onChanged() {

        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount) {

        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount, Object payload) {

        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {

        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {

        }

        @Override
        public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {

        }
    };

    // Typical constructor to initialize a recycler view from code. The other two constructors are
    // to initialize the constructor from xml.
    public CustomRecyclerView(Context context) {
        super(context);
    }

    public CustomRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void setAdapter(Adapter adapter) {
        super.setAdapter(adapter);

        // Whenever our recycler view has its adapter set, we'll modify the adapter's usual
        // behavior by having it register our adapter data observer. Every time set adapter is called
        // our adapter changes and we fire off the observer's onChange method that we defined above.
        if (adapter != null) {
            adapter.registerAdapterDataObserver(adapterDataObserver);
        }

        adapterDataObserver.onChanged();
    }

    public void hideIfEmpty(View... views) {
        // If our recycler view is empty, meaning no data, hide these views. The views we hide
        // are the views meant for when our recycler view has data.
        viewsToShowWhenRecyclerViewHasData = Arrays.asList(views);
    }

    public void showIfEmpty(View... views) {
        // If our recycler view is empty, meaning no data, show these views. The views we show
        // are the views meant for when our recycler view has no data.
        viewsToShowWhenRecyclerViewIsEmpty = Arrays.asList(views);
    }
}
