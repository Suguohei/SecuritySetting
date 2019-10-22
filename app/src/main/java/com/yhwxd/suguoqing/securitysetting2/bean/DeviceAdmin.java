package com.yhwxd.suguoqing.securitysetting2.bean;


import android.app.admin.DeviceAdminReceiver;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class DeviceAdmin extends DeviceAdminReceiver {

    private static final String TAG = "DeviceAdmin";



    @Override
    public DevicePolicyManager getManager(Context context) {
        return super.getManager(context);
    }

    @Override
    public ComponentName getWho(Context context) {
        return super.getWho(context);
    }

    /**
     * 禁用
     */
    @Override
    public void onDisabled(Context context, Intent intent) {

        Toast.makeText(context, "设备管理正在关闭，您可以卸载它了", Toast.LENGTH_SHORT).show();

        super.onDisabled(context, intent);
    }

    @Override
    public CharSequence onDisableRequested(Context context, Intent intent) {
        Log.d(TAG, "onDisableRequested: 当激活取消的时候，会通知这里");
        return super.onDisableRequested(context, intent);
    }
    /**
     * 激活
     */
    @Override
    public void onEnabled(Context context, Intent intent) {

        Toast.makeText(context, "设备管理正在启动", Toast.LENGTH_SHORT).show();

        super.onEnabled(context, intent);
    }


    @Override
    public void onPasswordChanged(Context context, Intent intent) {
        super.onPasswordChanged(context, intent);
        Toast.makeText(context, "设备管理：密码己经改变",Toast.LENGTH_LONG).show();

    }
    @Override
    public void onPasswordFailed(Context context, Intent intent) {
        super.onPasswordFailed(context, intent);
        Toast.makeText(context, "设备管理：改变密码失败",Toast.LENGTH_LONG).show();
    }
    @Override
    public void onPasswordSucceeded(Context context, Intent intent) {
        super.onPasswordSucceeded(context, intent);
        Toast.makeText(context, "设备管理：改变密码成功",Toast.LENGTH_LONG).show();
    }
    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
    }
    @Override
    public IBinder peekService(Context myContext, Intent service) {
        //Logger.d("------" + "peekService" + "------");
        return super.peekService(myContext, service);
    }
}
