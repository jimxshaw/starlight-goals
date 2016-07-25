package me.jimmyshaw.starlightgoals.adapters;

public interface CompleteListener {
    // This method takes in a row item position and marks that row item at that
    // position as complete.
    void onComplete(int position);
}
