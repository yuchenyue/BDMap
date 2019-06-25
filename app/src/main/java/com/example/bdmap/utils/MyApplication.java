package com.example.bdmap.utils;

import android.app.Application;
import android.content.Context;

public class MyApplication extends Application {

    private static Context context;
    private static String city;
    public MyApplication(){

    }
    @Override
    public void onCreate() {
        super.onCreate();
        context = this.getApplicationContext();
    }
    public static Context getContext() {
        return context;
    }

    public static String getCity() {
        return city;
    }

    public static void setCity(String city) {
        MyApplication.city = city;
    }
}
