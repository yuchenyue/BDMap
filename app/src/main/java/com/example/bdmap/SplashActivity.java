package com.example.bdmap;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.bdmap.presenter.Presenter;
import com.example.bdmap.service.LocationService;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class SplashActivity extends AppCompatActivity {

    Button button;
    LocationManager locationManager;
    boolean ok;
    Presenter presenter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SplashActivity.this,MainActivity.class));
                finish();
            }
        });
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        ok = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        pre();
        closeAndroidPDialog();
        startService(new Intent(this,LocationService.class));
    }

    /**
     * 权限
     */
    private void pre() {
        List<String> permissionList = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(SplashActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (ContextCompat.checkSelfPermission(SplashActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        }
        if (ContextCompat.checkSelfPermission(SplashActivity.this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.READ_PHONE_STATE);
        }
        if (ContextCompat.checkSelfPermission(SplashActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (!permissionList.isEmpty()) {
            String[] permissions = permissionList.toArray(new String[permissionList.size()]);
            ActivityCompat.requestPermissions(SplashActivity.this, permissions, 1);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if (!Settings.System.canWrite(this)){
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("修改系统设置");
                builder.setMessage("我们的应用需要您授权\"修改系统设置\"的权限,请点击\"设置\"确认开启");
                builder.setPositiveButton("Y", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS,Uri.parse("package:"+getPackageName()));
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                });
                builder.setCancelable(false);
                builder.show();
            }
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
//            Toast.makeText(this, "系统检测到未开启GPS定位服务",Toast.LENGTH_SHORT).show();
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
