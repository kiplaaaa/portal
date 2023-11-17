package com.example.reportgeneration;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

public class YourApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true); // Enable local data persistence (optional)
    }
}
