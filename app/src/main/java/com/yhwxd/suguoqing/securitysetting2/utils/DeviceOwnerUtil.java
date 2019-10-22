package com.yhwxd.suguoqing.securitysetting2.utils;

import android.annotation.TargetApi;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.graphics.Bitmap;
import android.os.Build;
import android.util.Log;

public class DeviceOwnerUtil {
    private static final String TAG = "DeviceOwnerUtil";

    /*
    * 重置开机密码
    * */
    public static boolean resetPassword (DevicePolicyManager devicePolicyManager,String pwd) {

        try {
            return devicePolicyManager.resetPassword(pwd,DevicePolicyManager.RESET_PASSWORD_REQUIRE_ENTRY);
        }catch (SecurityException e){
            e.printStackTrace();
            Log.d(TAG, "resetPassword: application does not own an active administrator");
        }catch (IllegalStateException e){
            e.printStackTrace();
            Log.d(TAG, "resetPassword: the calling user is locked or has a managed profile.");
        }

        return false;
    }

    /*
    * 重启
    * */
    @TargetApi(Build.VERSION_CODES.N)
    public static void reboot (DevicePolicyManager devicePolicyManager, ComponentName admin){
        try {
            devicePolicyManager.reboot(admin);
        }catch (SecurityException e){
            e.printStackTrace();
            Log.d(TAG, "reboot: if device has an ongoing call.");
        }catch (IllegalStateException e){
            e.printStackTrace();
            Log.d(TAG, "reboot: if admin is not a device owner.");
        }
    }

    /*
    * 判断是不是devices owner
    * */
    public static boolean isDeviceOwnerApp(DevicePolicyManager devicePolicyManager,String packageName){
        return devicePolicyManager.isDeviceOwnerApp(packageName);
    }

    /*
    * 判断app是否隐藏了
    * */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static boolean isApplicationHidden(DevicePolicyManager devicePolicyManager, ComponentName admin, String packageName){
        try{
            return devicePolicyManager.isApplicationHidden(admin,packageName);
        }catch (SecurityException e){
            e.printStackTrace();
        }

        return false;
    }

    /*
    * 判断是否静音了
    * */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static boolean isMasterVolumeMuted(DevicePolicyManager devicePolicyManager, ComponentName admin){
        try{
            return devicePolicyManager.isMasterVolumeMuted(admin);
        }catch (SecurityException e){
            e.printStackTrace();
        }

        return false;
    }

    /*
    * 判断应用是不是被禁止卸载
    * */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static boolean isUninstallBlocked(DevicePolicyManager devicePolicyManager, ComponentName admin,String packageName){
        try{
            return devicePolicyManager.isUninstallBlocked(admin,packageName);
        }catch (SecurityException e){
            e.printStackTrace();
        }

        return false;
    }

    /*
    * 设置app隐藏
    * */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static boolean setApplicationHidden(DevicePolicyManager devicePolicyManager, ComponentName admin, String packageName, boolean hidden){
        try{
            return devicePolicyManager.setApplicationHidden(admin,packageName,hidden);
        }catch (SecurityException e){
            e.printStackTrace();
        }

        return false;
    }


    /*
    * 设置锁屏显示的信息
    * */

    @TargetApi(Build.VERSION_CODES.N)
    public static void setDeviceOwnerLockScreenInfo(DevicePolicyManager devicePolicyManager, ComponentName admin, CharSequence info){
        try{
            devicePolicyManager.setDeviceOwnerLockScreenInfo(admin,info);
        }catch (SecurityException e){
            e.printStackTrace();
        }

    }

    /*
    * 设置静音
    * */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static void setMasterVolumeMuted (DevicePolicyManager devicePolicyManager, ComponentName admin, boolean on){
        try{

            devicePolicyManager.setMasterVolumeMuted(admin,on);
        }catch (SecurityException e){
            e.printStackTrace();
        }
    }


    /*
    * 设置状态栏下拉
    * */
    @TargetApi(Build.VERSION_CODES.M)
    public static boolean setStatusBarDisabled (DevicePolicyManager devicePolicyManager , ComponentName admin, boolean disabled){
        try{
            return devicePolicyManager.setStatusBarDisabled(admin,disabled);
        }catch (SecurityException e){
            e.printStackTrace();
        }

        return false;
    }

    /*
    * 设置不能卸载
    * */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static void setUninstallBlocked (DevicePolicyManager devicePolicyManager, ComponentName admin, String packageName, boolean uninstallBlocked){
        try{
            devicePolicyManager.setUninstallBlocked(admin,packageName,uninstallBlocked);
        }catch (SecurityException e){
            e.printStackTrace();
        }
    }

    /*
    * 设置UserIcon
    * */
    @TargetApi(Build.VERSION_CODES.M)
    public static void setUserIcon (DevicePolicyManager devicePolicyManager , ComponentName admin, Bitmap icon){
        try{
            devicePolicyManager.setUserIcon(admin,icon);
        }catch (SecurityException e){
            e.printStackTrace();
        }
    }

    /*
    * 是否可以被截屏
    * */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static boolean getScreenCaptureDisabled(DevicePolicyManager devicePolicyManager, ComponentName admin){
        try{
            return devicePolicyManager.getScreenCaptureDisabled(admin);
        }catch (SecurityException e){
            e.printStackTrace();
        }

        return false;
    }

    /*
    *
    * 设置截屏
    * */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static void setScreenCaptureDisabled(DevicePolicyManager devicePolicyManager, ComponentName admin, boolean isDisable){
        try{
            devicePolicyManager.setScreenCaptureDisabled(admin,!isDisable);
        }catch (SecurityException e){
            e.printStackTrace();
        }
    }



}
