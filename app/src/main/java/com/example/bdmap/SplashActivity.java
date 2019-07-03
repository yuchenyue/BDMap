package com.example.bdmap;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.bdmap.permission.PermissionUtils;
import com.example.bdmap.permission.request.IRequestPermissions;
import com.example.bdmap.permission.request.RequestPermissions;
import com.example.bdmap.permission.requestresult.IRequestPermissionsResult;
import com.example.bdmap.permission.requestresult.RequestPermissionsResultSetApp;
import com.example.bdmap.presenter.Presenter;
import com.example.bdmap.service.LocationService;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class SplashActivity extends AppCompatActivity {

    Button button;
    LocationManager locationManager;
    boolean ok;
    Presenter presenter;
    IRequestPermissions requestPermissions = RequestPermissions.getInstance();//动态权限请求
    IRequestPermissionsResult requestPermissionsResult = RequestPermissionsResultSetApp.getInstance();//动态权限请求结果处理


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chackpremissions();
                startActivity(new Intent(SplashActivity.this,MainActivity.class));
                finish();
            }
        });
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        ok = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        chackpremissions();
        closeAndroidPDialog();
        startService(new Intent(this,LocationService.class));
    }

    /**
     * 权限
     */
    private void chackpremissions(){
        if (!requestPermissions()) {

        }
        if (ok){

        }else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("定位设置");
            builder.setMessage("我们的应用需要您授权\"定位设置\"的权限,请点击\"设置\"确认开启");
            builder.setPositiveButton("Y", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent();
                    intent.setAction(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);
                }
            });
            builder.setCancelable(false);
            builder.show();
            presenter.tos("系统检测到未开启GPS定位服务");
        }
    }

    private boolean requestPermissions() {
        String[] permissions = new String[]{
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.READ_PHONE_STATE};
        return requestPermissions.requestPermissions(this, permissions, PermissionUtils.ResultCode1);
    }

    //用户授权操作结果（可能授权了，也可能未授权）
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //用户给APP授权的结果
        //判断grantResults是否已全部授权，如果是，执行相应操作，如果否，提醒开启权限
//        requestPermissionsResult.doRequestPermissionsResult(this,permissions,grantResults);
        if (requestPermissionsResult.doRequestPermissionsResult(this, permissions, grantResults)) {
            //请求的权限全部授权成功，此处可以做自己想做的事了
            //输出授权结果
            Toast.makeText(getApplicationContext(), "授权成功", Toast.LENGTH_LONG).show();

        } else {
            //输出授权结果

            Toast.makeText(getApplicationContext(), "请给APP授权，否则功能无法正常使用！", Toast.LENGTH_LONG).show();
        }
    }


    private void closeAndroidPDialog() {
        try {
            Class aClass = Class.forName("android.content.pm.PackageParser$Package");
            Constructor declaredConstructor = aClass.getDeclaredConstructor(String.class);
            declaredConstructor.setAccessible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            Class cls = Class.forName("android.app.ActivityThread");
            Method declaredMethod = cls.getDeclaredMethod("currentActivityThread");
            declaredMethod.setAccessible(true);
            Object activityThread = declaredMethod.invoke(null);
            Field mHiddenApiWarningShown = cls.getDeclaredField("mHiddenApiWarningShown");
            mHiddenApiWarningShown.setAccessible(true);
            mHiddenApiWarningShown.setBoolean(activityThread, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
