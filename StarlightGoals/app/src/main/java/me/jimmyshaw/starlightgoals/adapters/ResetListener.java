package me.jimmyshaw.starlightgoals.adapters;

// The purpose of this ResetListener is that we want the app to reset our filter back to our main activity's
// empty goals layout when realm database has no data. Most importantly, we want to reset the filter
// regardless which other filter is on at the time. The logic being if the database has no data then there
// wouldn't be any point to display anything else but the app's default filter state, which is filter off.
public interface ResetListener {
    void onReset();
}
