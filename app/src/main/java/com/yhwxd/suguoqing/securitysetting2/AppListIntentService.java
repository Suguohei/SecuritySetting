package com.yhwxd.suguoqing.securitysetting2;

import android.app.IntentService;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.yhwxd.suguoqing.securitysetting2.bean.AppInfo;
import com.yhwxd.suguoqing.securitysetting2.bean.DeviceAdmin;
import com.yhwxd.suguoqing.securitysetting2.utils.DeviceOwnerUtil;
import com.yhwxd.suguoqing.securitysetting2.utils.SpUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class AppListIntentService extends IntentService {
    private static final String TAG = "AppListIntentService";
    private final static String FIRST_INIT = "first_init";
    private PackageManager mPackageManager;
    private AppInfoManager appInfoManager;

    public AppListIntentService() {
        super("AppListIntentService");
    }


    @Override
    public void onCreate() {
        super.onCreate();
        mPackageManager = getPackageManager();
        appInfoManager = new AppInfoManager(mPackageManager,this);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        //判断是不是第一次进来
        boolean isInitDb = SpUtil.getInstance().getBoolean(FIRST_INIT, false);
        /*每次都获取手机上的所有应用*/
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        //mainIntent.setPackage(null);
        //List<ResolveInfo> list = mPackageManager.queryIntentActivities(mainIntent, PackageManager.GET_DISABLED_COMPONENTS);
        List<ResolveInfo> list = mPackageManager.queryIntentActivities(mainIntent, PackageManager.GET_UNINSTALLED_PACKAGES);
        Log.d(TAG, "onHandleIntent: "+list.size());
        if(isInitDb){
            List<ResolveInfo> appList = new ArrayList<>();
            List<AppInfo> dbList = appInfoManager.getAllAppInfo();//获取数据库列表
            //处理应用列表
            for(ResolveInfo resolveInfo : list){

                String packageName = resolveInfo.activityInfo.applicationInfo.packageName;
               // Log.d(TAG, "onHandleIntent: packagename"+packageName);
                if(!packageName.equals("com.android.inputmethod.latin")
                        &&!packageName.equals("com.google.android.inputmethod.pinyin")
                        &&!packageName.equals("com.qualcomm.qti.modemtestmode")
                        &&!packageName.equals("com.qualcomm.qti.sensors.qsensortest")
                        /* MODIFIED-BEGIN by tianhai.sun, 2019-04-03,BUG-7604530*/
                        &&!packageName.equals("com.quicinc.cne.settings")
                        &&!packageName.equals("com.android.stk")
                        &&!packageName.equals("com.yhwxd.suguoqing.securitysetting2")

                ){
                    appList.add(resolveInfo);
                }

            }

            if(appList.size() > dbList.size()){//说明增加了应用
                Log.d(TAG, "onHandleIntent: something install");
                List<ResolveInfo> reslist = new ArrayList<>();
                HashMap<String, AppInfo> hashMap = new HashMap<>();
                for (AppInfo info : dbList) {
                    hashMap.put(info.getPackageName(), info);
                }
                for (ResolveInfo info : appList) {
                    if (!hashMap.containsKey(info.activityInfo.packageName)) {
                        reslist.add(info);
                    }
                }
                try {
                    if (reslist.size() != 0)
                        appInfoManager.insertAppInfo(reslist); //将剩下不同的插入数据库
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
            }else if(appList.size() < dbList.size()){
                Log.d(TAG, "onHandleIntent: something has been uninstall");
                List<AppInfo> commlist = new ArrayList<>();
                HashMap<String, ResolveInfo> hashMap = new HashMap<>();
                for (ResolveInfo info : appList) {
                    hashMap.put(info.activityInfo.packageName, info);
                }
                for (AppInfo info : dbList) {
                    if (!hashMap.containsKey(info.getPackageName())) {
                      commlist.add(info);
                    }
                }
                //Logger.d("有应用卸载，个数是 = " + dbList.size());
                if (commlist.size() != 0)
                    appInfoManager.deleteAppInfo(commlist);//将多的从数据库删除
            }else {
                Log.d(TAG, "onHandleIntent: 不多不少");
            }
        }else {
            //数据库只插入一次
            SpUtil.getInstance().putBoolean(FIRST_INIT, true);
            try {
                appInfoManager.insertAppInfo(list);    //插入数据库
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    /*
     * drawable转化成bitmap
     * */
    private Bitmap bitmap;
    private void drawableToBitamp(Drawable drawable)
    {
        int w = drawable.getIntrinsicWidth();
        int h = drawable.getIntrinsicHeight();
        // System.out.println("Drawable转Bitmap");
        Bitmap.Config config =
                drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                        : Bitmap.Config.RGB_565;
        bitmap = Bitmap.createBitmap(w,h,config);
        //注意，下面三行代码要用到，否在在View或者surfaceview里的canvas.drawBitmap会看不到图
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, w, h);
        drawable.draw(canvas);
    }


}
