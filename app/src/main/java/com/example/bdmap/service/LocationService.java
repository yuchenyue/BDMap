package com.example.bdmap.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.bdmap.contract.MainContract;

public class LocationService extends Service implements MainContract.MainView{

    public static final String TAG = "LocationService";
    @Override
    public void onCreate() {
        Log.d(TAG,"aaa"+"服务开启");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return myBinder;
    }
    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    private MyBinder myBinder = new MyBinder();

    public class MyBinder extends Binder {
        public LocationService getService() {
            return LocationService.this;
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean stopService(Intent name) {
        return super.stopService(name);
    }
}
