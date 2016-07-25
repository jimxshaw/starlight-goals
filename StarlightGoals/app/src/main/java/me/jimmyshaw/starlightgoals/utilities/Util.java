package me.jimmyshaw.starlightgoals.utilities;

import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.View;

import java.util.List;

public class Util {
    public static void showViews(List<View> views) {
        for (View view : views) {
            view.setVisibility(View.VISIBLE);
        }
    }

    public static void hideViews(List<View> views) {
        for (View view : views) {
            view.setVisibility(View.GONE);
        }
    }

    public static void setBackground(View view, Drawable drawable) {
        // Certain methods for setting the background can or cannot be
        // used depending on the Android Api version number.
        if (greaterThanAndroidApi(15)) {
            view.setBackground(drawable);
        }
        else {
            view.setBackgroundDrawable(drawable);
        }
    }

    public static boolean greaterThanAndroidApi(int api) {
        return Build.VERSION.SDK_INT > api;
    }
}
