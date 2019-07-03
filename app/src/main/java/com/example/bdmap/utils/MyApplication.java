package com.example.bdmap.utils;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Process;
import java.util.ArrayList;
import java.util.List;

public class MyApplication extends Application {

    private static Context context;
    private static double latitude;
    private static double longitude;
    private static String city;
    private static float mCurrentX;
    private List<Activity> list = new ArrayList<Activity>();
    private static MyApplication exit;
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

    public static String getCity() {
        return city;
    }

    public static void setCity(String city) {
        MyApplication.city = city;
    }

    public static float getmCurrentX() {
        return mCurrentX;
    }

    public static void setmCurrentX(float mCurrentX) {
        MyApplication.mCurrentX = mCurrentX;
    }

    public static MyApplication getInstance(){
        if (null == exit){
            exit = new MyApplication();
        }
        return exit;
    }
    public void addActivity(Activity activity){
        list.add(activity);
    }
    public void exit(Context context){

        for (Activity activity : list){
            activity.finish();
        }
        Process.killProcess(Process.myPid());
        System.exit(0);
    }
}
