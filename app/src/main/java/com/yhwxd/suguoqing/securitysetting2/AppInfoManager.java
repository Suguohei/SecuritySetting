package com.yhwxd.suguoqing.securitysetting2;

import android.content.ContentValues;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

import com.yhwxd.suguoqing.securitysetting2.bean.AppInfo;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AppInfoManager {
    private PackageManager mPackageManager;
    private Context mContext;

    public AppInfoManager(PackageManager mPackageManager, Context mContext) {
        this.mPackageManager = mPackageManager;
        this.mContext = mContext;
    }

    /*
    * 查找所有
    * */
    public synchronized List<AppInfo> getAllAppInfo(){
        List<AppInfo> appInfos = DataSupport.findAll(AppInfo.class);
        return appInfos;
    }

    /*
    * 删除数据
    * */
    public synchronized void deleteAppInfo(List<AppInfo> appInfos){
        for(AppInfo appInfo : appInfos){
            DataSupport.deleteAll(AppInfo.class,"packageName = ?",appInfo.getPackageName());
        }
    }

    /*
    * 插入数据
    * */
    public synchronized void insertAppInfo(List<ResolveInfo> resolveInfos)throws PackageManager.NameNotFoundException{
        List<AppInfo> list = new ArrayList<>();

        for(ResolveInfo resolveInfo : resolveInfos){
            PackageManager packageManager = mContext.getPackageManager();
            AppInfo appInfo = new AppInfo();
            appInfo.setName((String) resolveInfo.loadLabel(packageManager));
            appInfo.setPackageName(resolveInfo.activityInfo.applicationInfo.packageName);
            appInfo.setHide(false);
            appInfo.setLock(false);

            String packageName = resolveInfo.activityInfo.applicationInfo.packageName;

            if(!packageName.equals("com.android.inputmethod.latin")
                    &&!packageName.equals("com.google.android.inputmethod.pinyin")
                    &&!packageName.equals("com.qualcomm.qti.modemtestmode")
                    &&!packageName.equals("com.qualcomm.qti.sensors.qsensortest")
                    /* MODIFIED-BEGIN by tianhai.sun, 2019-04-03,BUG-7604530*/
                    &&!packageName.equals("com.quicinc.cne.settings")
                    &&!packageName.equals("com.android.stk")
                    &&!packageName.equals("com.yhwxd.suguoqing.securitysetting2")

            ){
                list.add(appInfo);
            }


        }
        list = clearRepeatCommLockInfo(list); //去除重复数据
        DataSupport.saveAll(list);
    }



    /*
    * 除去重复的数据
    * */
    public static List<AppInfo> clearRepeatCommLockInfo(List<AppInfo> lockInfos) {
        HashMap<String, AppInfo> hashMap = new HashMap<>();
        for (AppInfo lockInfo : lockInfos) {
            if (!hashMap.containsKey(lockInfo.getPackageName())) {
                hashMap.put(lockInfo.getPackageName(), lockInfo);
            }
        }
        List<AppInfo> apps = new ArrayList<>();
        for (HashMap.Entry<String, AppInfo> entry : hashMap.entrySet()) {
            apps.add(entry.getValue());
        }
        return apps;
    }


    /*
    * 获取ishide的状态是不是为隐藏
    * */
    public boolean isHide(String packageName){
        List<AppInfo> appInfos = DataSupport.where("packageName = ?",packageName).find(AppInfo.class);
        for(AppInfo appInfo : appInfos){
            if (appInfo.isHide()){
                return true;
            }
        }
        return false;
    }

    /*
    * 设置ishide
    * */
    public void updateIsHide(String packageName,boolean isHide){
        ContentValues values = new ContentValues();
        values.put("isHide", isHide);
        DataSupport.updateAll(AppInfo.class,values,"packageName = ?",packageName);
    }

    /*
    * 获取isuninstall状态 是不是可卸载
    * */
    public  boolean isUninstall (String packageName){
        List<AppInfo> appInfos = DataSupport.where("packageName = ?",packageName).find(AppInfo.class);
        for(AppInfo appInfo : appInfos){
            if (appInfo.isUninstall()){
                return true;
            }
        }
        return false;
    }

    /*
    * 设置isuninstall
    * */
    public void updateIsUninstall(String packageName,boolean isUninstall){
        ContentValues values = new ContentValues();
        values.put("isUninstall", isUninstall);
        DataSupport.updateAll(AppInfo.class,values,"packageName = ?",packageName);
    }

}
