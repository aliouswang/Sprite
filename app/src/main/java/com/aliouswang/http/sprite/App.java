package com.aliouswang.http.sprite;

import android.app.Application;

import com.facebook.stetho.Stetho;

/**
 * Created by aliouswang on 16/1/13.
 */
public class App extends Application{

    @Override
    public void onCreate() {
        super.onCreate();
        Stetho.initializeWithDefaults(this);
    }
}
