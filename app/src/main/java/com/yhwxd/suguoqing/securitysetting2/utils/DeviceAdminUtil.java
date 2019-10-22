package com.yhwxd.suguoqing.securitysetting2.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.bluetooth.BluetoothAdapter;
import android.content.ComponentName;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.telephony.TelephonyManager;

public class DeviceAdminUtil {
    public static final int RESULT_ENABLE = 1;

    /*
    * 是否开启蓝牙
    * */
    public static boolean isOpenBlue(){
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        return adapter.isEnabled();
    }


    /*
    * 是否开启wifi
    * */
    public static boolean isOpenWifi(WifiManager manager){
        return manager.isWifiEnabled();
    }


    /*
    * 锁屏
    * */
    public static void lockNow (DevicePolicyManager devicePolicyManager){
        try {
            devicePolicyManager.lockNow();
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    /*
    * 是否打开了数据开关
    * */
    @TargetApi(Build.VERSION_CODES.O)
    public static boolean isOpenData(TelephonyManager manager){
        return  manager.getDataState() == TelephonyManager.DATA_CONNECTED ? true : false;
    }


    /*
    * 是否禁止了相机
    * */
    public static boolean isOpenCamera(DevicePolicyManager devicePolicyManager,ComponentName admin){
        try{
           return devicePolicyManager.getCameraDisabled(admin);
        }catch (Exception e){
            e.printStackTrace();
        }

        return false;
    }

    /*
    * 是否获取了管理员
    * */
    public static boolean isGetAdmin(DevicePolicyManager devicePolicyManager,ComponentName admin){
        try{
            return devicePolicyManager.isAdminActive(admin);
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    /*
    * 激活管理员
    * */
    public static void getAdmin(ComponentName mDeviceComponentName, Activity activity){
        Intent intent = new Intent(
                DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN,
                mDeviceComponentName);
        intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,
                "这里可以输入一些额外的说明,比如提示用户什么的:你必须获取管理员才可以做一下操作");
        activity.startActivityForResult(intent, RESULT_ENABLE);
    }

    /*
    * 取消管理员
    * */
    public static void cancelAdmin(DevicePolicyManager devicePolicyManager,ComponentName admin){
        try{
            devicePolicyManager.removeActiveAdmin(admin);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /*
    * 禁止相机
    * */
    public static void setCameraDisabled (DevicePolicyManager devicePolicyManager,ComponentName admin, boolean disabled){
        try{
            devicePolicyManager.setCameraDisabled(admin,disabled);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /*
    * 设置蓝牙
    * */
    public static void setBlue(boolean isOpen){
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        if(isOpen){
            adapter.enable();
        }else {
            adapter.disable();
        }
    }

    /*
    * 设置wifi
    * */
    public static void setWifi(WifiManager manager,boolean isOpen){
        manager.setWifiEnabled(isOpen);
    }

    /*
    * 设置数据流量
    * */
    @TargetApi(Build.VERSION_CODES.O)
    public static void setData(TelephonyManager telephonyManager, boolean isOpen){
        telephonyManager.setDataEnabled(isOpen);
    }
}
