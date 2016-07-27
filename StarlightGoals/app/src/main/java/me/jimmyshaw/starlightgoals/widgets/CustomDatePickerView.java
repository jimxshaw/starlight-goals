package me.jimmyshaw.starlightgoals.widgets;

import android.content.Context;
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
        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                break;
            case MotionEvent.ACTION_CANCEL:
                break;
        }

        // The boolean specifies whether or not the touch event has been consumed. True for yes or
        // false for no.
        return true;
    }
}
