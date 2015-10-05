package de.smv_am_mpg.smvapp;

import android.app.Application;

import com.parse.Parse;

public class SmvApplication extends Application{
    @Override
    public void onCreate() {
        super.onCreate();
        Parse.initialize(this, "1aBJyf0oXUSrEKqDndGZYHFm3SnwUnXsMqN7PhyM",
                "8NL6rHsi5Nh7hQVLB6smRoJRZqCOpwZVVnR6uBno");
    }
}
