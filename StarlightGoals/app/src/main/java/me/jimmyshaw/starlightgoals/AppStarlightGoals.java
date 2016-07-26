package me.jimmyshaw.starlightgoals;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import me.jimmyshaw.starlightgoals.adapters.Filter;

// This application configuration class is needed to primarily setup Realm's configuration. If we don't
// configure Realm on start up then we'll have to configure Realm every time we want to use it
// in any of our activities or fragments.
// Other methods are used to work with shared preferences and to change the overall font typeface.
public class AppStarlightGoals extends Application {

    public static final String FILTER = "FILTER";

    @Override
    public void onCreate() {
        super.onCreate();

        RealmConfiguration configuration = new RealmConfiguration.Builder(this).build();
        Realm.setDefaultConfiguration(configuration);
    }

    public static void saveToSharedPreferences(Context context, int filterOption) {
        // The difference between getPreferences and getSharedPreferences is the later is meant for
        // multiple shared preferences instances and it forces you to provide a file name to specify
        // the one you want.
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(FILTER, filterOption);
        // There are two ways to commit values to shared preferences, commit or apply. The difference
        // is that commit is synchronous and apply is asynchronous.
        editor.apply();
    }

    public static int loadFromSharedPreferences(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        // Our default filter option would be off.
        int filterOption = preferences.getInt(FILTER, Filter.OFF);
        return filterOption;
    }
}
