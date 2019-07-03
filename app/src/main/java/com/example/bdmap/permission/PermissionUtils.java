package com.example.bdmap.permission;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PermissionUtils {
    public static int ResultCode1 = 100;//权限请求码
    public static int ResultCode2 = 200;//权限请求码
    public static int ResultCode3 = 300;//权限请求码
    public static String PermissionTip1 = "亲爱的用户 \n\n软件部分功能需要请求您的手机权限，请允许以下权限：\n\n";//权限提醒
    public static String PermissionTip2 = "\n请到 “应用信息 -> 权限” 中授予！";//权限提醒
    public static String PermissionDialogPositiveButton = "去手动授权";
    public static String PermissionDialogNegativeButton = "取消";

    private static PermissionUtils permissionUtils;

    public static PermissionUtils getInstance() {
        if (permissionUtils == null) {
            permissionUtils = new PermissionUtils();
        }
        return permissionUtils;
    }

    private HashMap<String, String> permissions;

    public HashMap<String, String> getPermissions() {
        if (permissions == null) {
            permissions = new HashMap<>();
            initPermissions();
        }
        return permissions;
    }

    private void initPermissions() {
        //使用步行AR导航，配置Camera权限
        permissions.put("android.permission.CAMERA", "--相机/拍照");
        //文件存取
        permissions.put("android.permission.READ_EXTERNAL_STORAGE", "--文件存储");
        permissions.put("android.permission.WRITE_EXTERNAL_STORAGE", "--文件存储");
        //获取设备网络状态，禁用后无法获取网络状态
        permissions.put("android.permission.ACCESS_NETWORK_STATE", "--设备网络状态");
        //网络权限，当禁用后，无法进行检索等相关业务
        permissions.put("android.permission.INTERNET", "--网络权限");
        //读取设备硬件信息，统计数据
        permissions.put("android.permission.READ_PHONE_STATE", "--设备硬件信息");
        //读取系统信息，包含系统版本等信息，用作统计
        permissions.put("com.android.launcher.permission.READ_SETTINGS", "--系统信息");
        //获取设备的网络状态，鉴权所需网络代理
        permissions.put("android.permission.ACCESS_WIFI_STATE", "--设备的网络状态");
        //获取统计数据 -->
        permissions.put("android.permission.WRITE_SETTINGS", "--统计数据");
        //鉴权所需该权限获取进程列表
        permissions.put("android.permission.GET_TASKS", "--进程列表");
        //网络定位 -->
        permissions.put("android.permission.ACCESS_COARSE_LOCATION", "--网络定位");
        //GPS定位 -->
        permissions.put("android.permission.ACCESS_FINE_LOCATION", "--GPS定位");
        //这个权限用于获取wifi的获取权限，wifi信息会用来进行网络定位 -->
        permissions.put("android.permission.CHANGE_WIFI_STATE", "--设备硬件信息");
        permissions.put("android.permission.FOREGROUND_SERVICE", "--设备硬件信息");

    }


    /**
     * 获得权限名称集合（去重）
     *
     * @param permission 权限数组
     * @return 权限名称
     */
    public String getPermissionNames(List<String> permission) {
        if (permission == null || permission.size() == 0) {
            return "\n";
        }
        StringBuilder sb = new StringBuilder();
        List<String> list = new ArrayList<>();
        HashMap<String, String> permissions = getPermissions();
        for (int i = 0; i < permission.size(); i++) {
            String name = permissions.get(permission.get(i));
            if (name != null && !list.contains(name)) {
                list.add(name);
                sb.append(name);
                sb.append("\n");
            }
        }
        return sb.toString();
    }

}