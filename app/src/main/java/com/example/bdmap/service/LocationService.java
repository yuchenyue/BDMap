package com.example.bdmap.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.example.bdmap.contract.MainContract;
import com.example.bdmap.presenter.Presenter;
import com.example.bdmap.utils.MyApplication;

import java.util.ArrayList;
import java.util.List;

public class LocationService extends Service implements MainContract.MainView{

    public static final String TAG = "LocationService";
    public LocationClient mLocationClient;
    public BDAbstractLocationListener myListener = new MyLocationListener();
    private Presenter presenter;
    @Override
    public void onCreate() {
        initLocation();
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

    /**
     * 配置定位参数
     */
    private void initLocation() {
        mLocationClient = new LocationClient(getApplicationContext());     //声明LocationClient类
        mLocationClient.registerLocationListener(myListener);    //注册监听函数
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系
        option.setScanSpan(3000);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
        option.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation
        option.setNeedDeviceDirect(true);//返回的定位结果包含手机机头的方向
        option.setLocationNotify(false);//可选，默认false，设置是否当GPS有效时按照1S/1次频率输出GPS结果
        option.setIgnoreKillProcess(true);//可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        option.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        option.SetIgnoreCacheException(true);//可选，默认false，设置是否收集CRASH信息，默认收集
        option.setOpenGps(true);//可选，默认false,设置是否使用gps
        option.setOpenAutoNotifyMode();
        option.setOpenAutoNotifyMode(2000, 1, LocationClientOption.LOC_SENSITIVITY_HIGHT);
        option.setEnableSimulateGps(false);//可选，默认false，设置是否需要过滤GPS仿真结果，默认需要
        option.setWifiCacheTimeOut(5 * 60 * 1000);
        mLocationClient.setLocOption(option);
        mLocationClient.start();//开启定位
    }

    /**
     * 实现BDLocationListener接口,BDLocationListener为结果监听接口，异步获取定位结果
     */
    private class MyLocationListener extends BDAbstractLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            float radius = location.getRadius();
            String city = location.getCity();
//            Log.d(TAG,"aaa" + "定位信息1" + latitude);
            MyApplication.setLatitude(latitude);
            MyApplication.setLongitude(longitude);
            MyApplication.setRadius(radius);
            MyApplication.setCity(city);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mLocationClient.stop();
    }
}
