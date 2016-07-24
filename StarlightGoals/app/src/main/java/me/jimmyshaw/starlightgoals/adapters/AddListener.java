package me.jimmyshaw.starlightgoals.adapters;

// Our recycler view footer holder in our AdapterGoals class has an Add button that behaves exactly
// like the main activity view's Add button. Main activity's Add button can easily launch our
// Add Dialog with the showDialogAddAGoal method. How does our footer Add button launch the same Add
// Dialog though? We can't put the showDialogAddAGoal method inside AdapterGoals because the
// purpose of an adapter is to display items, not manage dialogs or fragments. We could get a reference
// to the activity by creating a constructor in our adapter that takes in main activity as a parameter
// and as a result be able to call showDialogAddAGoal that way. That's a bad idea because our main
// activity will keep getting created and destroyed since it's following a lifecycle but our adapter
// follows no such lifecycle.
// The only other option is to use an interface that's common between the activity and the adapter
// and somehow pass data between them using the interface. This is the better option.

// We define this listener interface with one method, Add. The listener and its method's implementation is
// inside our main activity. Whenever our adapter is instantiated in main activity, we'll pass
// in the listener into the adapter's constructor. Within the adapter, we'll then have access to the
// implementation of the Add method, which launches the Add Dialog.
public interface AddListener {
    void add();
}
