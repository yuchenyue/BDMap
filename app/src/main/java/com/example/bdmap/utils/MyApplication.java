package com.example.bdmap.utils;

import android.app.Application;
import android.content.Context;

public class MyApplication extends Application {

    private static Context context;
    private static double latitude;
    private static double longitude;
    private static float radius;
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

    public static double getLatitude() {
        return latitude;
    }

    public static void setLatitude(double latitude) {
        MyApplication.latitude = latitude;
    }

    public static double getLongitude() {
        return longitude;
    }

    public static void setLongitude(double longitude) {
        MyApplication.longitude = longitude;
    }

    public static float getRadius() {
        return radius;
    }

    public static void setRadius(float radius) {
        MyApplication.radius = radius;
    }

    public static String getCity() {
        return city;
    }

    public static void setCity(String city) {
        MyApplication.city = city;
    }
}
