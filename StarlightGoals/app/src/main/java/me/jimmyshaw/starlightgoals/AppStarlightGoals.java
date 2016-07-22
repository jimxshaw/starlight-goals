package me.jimmyshaw.starlightgoals;

import android.app.Application;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class AppStarlightGoals extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        RealmConfiguration configuration = new RealmConfiguration.Builder(this).build();
        Realm.setDefaultConfiguration(configuration);
    }
}
