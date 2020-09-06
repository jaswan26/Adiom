package com.homeautomation.adiom;

import android.app.Application;

public class MyApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        TypefaceUtil.overrideFont(getApplicationContext(), "SERIF", "Montserrat-Regular.ttf"); // font from assets: "assets/fonts/Roboto-Regular.ttf
    }
}
