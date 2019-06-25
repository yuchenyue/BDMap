package com.example.bdmap;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;

import com.example.bdmap.service.LocationService;

public class BaseActivity extends AppCompatActivity {

    public static boolean isBound = false;
    public LocationService locationService;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * 绑定Service
     */
    public ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            LocationService.MyBinder myBinder = (LocationService.MyBinder) service;
            locationService = myBinder.getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            locationService = null;
            isBound = false;
        }
    };

    /**
     * 绑定服务
     */
    public void bindLocationService() {
        if (!isBound) {
            Intent intent = new Intent(this, LocationService.class);
            bindService(intent, serviceConnection, Service.BIND_AUTO_CREATE);
            isBound = true;
        }
    }

    /**
     * 解绑服务
     */
    public void unbindLocationService() {
        if (isBound) {
            unbindService(serviceConnection);
            isBound = false;
        }
    }
}
