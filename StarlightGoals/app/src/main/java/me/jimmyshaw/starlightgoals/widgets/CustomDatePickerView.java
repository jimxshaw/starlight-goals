package me.jimmyshaw.starlightgoals.widgets;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.jimmyshaw.starlightgoals.R;

// This class extends LinearLayout because our custom date picker layout xml file has the root
// element of LinearLayout.
public class CustomDatePickerView extends LinearLayout implements View.OnTouchListener {

    @BindView(R.id.text_view_month)
    TextView textViewMonth;

    @BindView(R.id.text_view_day)
    TextView textViewDay;

    @BindView(R.id.text_view_year)
    TextView textViewYear;

    private Calendar calendar;

    private SimpleDateFormat simpleDateFormat;

    public static final String TAG = "Jim";

    // These int variables represent the boundaries of our drawable text views.
    public static final int LEFT = 0;
    public static final int TOP = 1;
    public static final int RIGHT = 2;
    public static final int BOTTOM = 3;

    // We have 3 constructors. The last two constructors are needed if we want to create our
    // custom date picker view from xml as opposed to in code.
    public CustomDatePickerView(Context context) {
        super(context);
        initialize(context);
    }

    public CustomDatePickerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(context);
    }

    public CustomDatePickerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(context);
    }

    private void initialize(Context context) {
        // The purpose of this initialize method is to use the LayoutInflater to inflate the specific
        // custom date picker layout and bind it to this specific custom date picker class.
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_add_a_goal_custom_date_picker, this); // The this keyword specifies this class is what's linked with this layout.
        // An alternative to using the default Java calendar would be the JodaTime for Android library.
        calendar = Calendar.getInstance();
        // We instantiate a SimpleDateFormat class in order to set how we'd like our months to be displayed.
        // The actual updating of the month format will take place in our updateCalendar method right
        // before we set the text of the month text view.
        simpleDateFormat = new SimpleDateFormat("MMM");
    }

    private void updateCalendar(int month, int day, int year, int hour, int minute, int second) {
        // Our goal's deadline date is in MM/DD/YYYY format. We get an instance of calendar above and set
        // month, day and year with the user input values from the date picker widget. Even though
        // our app isn't exact in specifying the time, we have to set those fields to a value anyway
        // in order to pass in the date to our Drop object as time-in-milliseconds.

        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, day);
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.HOUR, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, second);

        textViewMonth.setText(simpleDateFormat.format(calendar.getTime()));
        textViewDay.setText(String.valueOf(day));
        textViewYear.setText(String.valueOf(year));
    }

    public long getTime() {
        return calendar.getTimeInMillis();
    }

    private void processTouchEvents(TextView textView, MotionEvent motionEvent) {
        // To contain a touch event to a particular region of the text view drawable, we must take
        // the boundaries and calculate with them accordingly.
        Drawable[] drawables = textView.getCompoundDrawables();

        if (hasTopDrawable(drawables) && hasBottomDrawable(drawables)) {
            // Get top and bottom boundaries, which are rectangles, of our text view drawable.
            Rect boundsTop = drawables[TOP].getBounds();
            Rect boundsBottom = drawables[BOTTOM].getBounds();

            // Get coordinates of the motionEvent.
            float x = motionEvent.getX();
            float y = motionEvent.getY();

            // Find out which drawable, top or bottom, was clicked.
            // If top drawable was clicked then perform all the action related to incrementing the
            // date value, be it month, day or year. This would apply if the bottom drawable was
            // clicked but the date value would decrement.
            // The usage of if-else if-else is key because we only want one drawable to be clicked.
            // If we wanted both top and bottom and even the middle of the drawable to be clicked
            // simultaneously then we'd have separate if conditions.
            if (wasTopDrawableClicked(textView, boundsTop.height(), x, y)) {
                if (isActionDown(motionEvent)) {
                    // Increment the value here.
                    increment(textView.getId());
                }
            }
            else if (wasBottomDrawableClicked(textView, boundsBottom.height(), x, y)) {
                if (isActionDown(motionEvent)) {
                    // Decrement the value here.
                    decrement(textView.getId());
                }
            }
            else {
                // This condition occurs when the middle of the text view was clicked.

            }
        }
    }

    private boolean hasTopDrawable(Drawable[] drawables) {
        return drawables[TOP] != null;
    }

    private boolean wasTopDrawableClicked(TextView textView, int drawableHeight, float x, float y) {
        // Width of the text view - padding right.
        int xmax = textView.getWidth() - textView.getPaddingRight();
        // Padding left.
        int xmin = textView.getPaddingLeft();
        // Padding top + drawable height.
        int ymax = textView.getPaddingTop() + drawableHeight;
        // Padding top of the text view.
        int ymin = textView.getPaddingTop();

        return x > xmin && x < xmax && y > ymin && y < ymax;
    }

    private boolean hasBottomDrawable(Drawable[] drawables) {
        return drawables[BOTTOM] != null;
    }

    private boolean wasBottomDrawableClicked(TextView textView, int drawableHeight, float x, float y) {
        // Width of the text view - padding right.
        int xmax = textView.getWidth() - textView.getPaddingRight();
        // Padding left.
        int xmin = textView.getPaddingLeft();
        // Total height of the text view - padding bottom.
        int ymax = textView.getHeight() - textView.getPaddingBottom();
        // Total height of the text view - the height of the drawable region.
        int ymin = ymax - drawableHeight;

        return x > xmin && x < xmax && y > ymin && y < ymax;
    }

    private boolean isActionDown(MotionEvent motionEvent) {
        // ACTION_DOWN is when we first touch the screen.
        // ACTION_MOVE takes place after ACTION_DOWN when we move our finger or we hold our finger.
        // ACTION_UP takes place after we released our touch action.
        // ACTION_CANCEL occurs when the current touch action is aborted (usually by Android itself).
        return motionEvent.getAction() == MotionEvent.ACTION_DOWN;
    }

    private void increment(int id) {
        // The id of the text view to be incremented is passed in.
        switch (id) {
            case R.id.text_view_month:
                // Get the calendar month and increment it by 1.
                calendar.add(Calendar.MONTH, 1);
                break;
            case R.id.text_view_day:
                calendar.add(Calendar.DAY_OF_MONTH, 1);
                break;
            case R.id.text_view_year:
                calendar.add(Calendar.YEAR, 1);
                break;
        }
        // Simply incrementing the calendar value in the background isn't enough. We must show the
        // text view change visually. We must update it.
        refreshCalendarUI(calendar);
    }

    private void decrement(int id) {
        // The id of the text view to be decremented is passed in.
        switch (id) {
            case R.id.text_view_month:
                // Get the calendar month and decrement it by 1.
                calendar.add(Calendar.MONTH, -1);
                break;
            case R.id.text_view_day:
                calendar.add(Calendar.DAY_OF_MONTH, -1);
                break;
            case R.id.text_view_year:
                calendar.add(Calendar.YEAR, -1);
                break;
        }
        // Simply decrementing the calendar value in the background isn't enough. We must show the
        // text view change visually. We must update it.
        refreshCalendarUI(calendar);
    }

    private void refreshCalendarUI(Calendar calendar) {
        textViewMonth.setText(simpleDateFormat.format(calendar.getTime()));

        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int year = calendar.get(Calendar.YEAR);

        textViewDay.setText(String.valueOf(day));
        textViewYear.setText(String.valueOf(year));
    }

    @Override
    protected void onFinishInflate() {
        // This special method manages our views after the layout file as been inflated. The work
        // done in this method is considered the final phase of the overall inflation process.
        super.onFinishInflate();
        ButterKnife.bind(this);

        textViewMonth.setOnTouchListener(this);
        textViewDay.setOnTouchListener(this);
        textViewYear.setOnTouchListener(this);

        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int year = calendar.get(Calendar.YEAR);

        // After initializing our widgets above, we call the updateCalendar method with the
        // date parameters passed in after getting them from the calendar member variable.
        // Since our date picker doesn't take into account the hour, minute or second, we'll pass 0s.
        updateCalendar(month, day, year, 0, 0, 0);
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        // The passed in view is the view that was touched but which one? That's determined by a
        // switch statement that takes in the view's id.

        switch (view.getId()) {
            case R.id.text_view_month:
                processTouchEvents(textViewMonth, motionEvent);
                break;
            case R.id.text_view_day:
                processTouchEvents(textViewDay, motionEvent);
                break;
            case R.id.text_view_year:
                processTouchEvents(textViewYear, motionEvent);
                break;
        }
        // The boolean specifies whether or not the touch event has been consumed. True for yes or
        // false for no.
        return true;
    }
}
