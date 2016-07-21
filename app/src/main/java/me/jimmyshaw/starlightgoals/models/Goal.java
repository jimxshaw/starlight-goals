package me.jimmyshaw.starlightgoals.models;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Goal extends RealmObject {
    // The reason why dateAdded and dateDue are long types is because their dates will be stored as
    // time-in-milliseconds. It's easier to perform calculations such as find out many days or months
    // are remaining from the user's present until the dateDue. Those calculations are done with the
    // DateUtils class.
    @PrimaryKey
    private long dateAdded;
    private long dateDue;
    private String goalText;
    private boolean completed;

    public Goal() {

    }

    public Goal(long dateAdded, long dateDue, String goalText, boolean completed) {
        this.dateAdded = dateAdded;
        this.dateDue = dateDue;
        this.goalText = goalText;
        this.completed = completed;
    }

    public String getGoal() {
        return goalText;
    }

    public void setGoal(String goal) {
        this.goalText = goalText;
    }

    public long getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(long dateAdded) {
        this.dateAdded = dateAdded;
    }

    public long getDateDue() {
        return dateDue;
    }

    public void setDateDue(long dateDue) {
        this.dateDue = dateDue;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }
}
