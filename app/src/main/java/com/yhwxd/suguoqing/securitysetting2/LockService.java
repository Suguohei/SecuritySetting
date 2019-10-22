package com.yhwxd.suguoqing.securitysetting2;

import android.app.ActivityManager;
import android.app.Service;
import android.app.usage.UsageEvents;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;

import com.yhwxd.suguoqing.securitysetting2.activity.ScreetActivity;
import com.yhwxd.suguoqing.securitysetting2.utils.LockUtil;
import com.yhwxd.suguoqing.securitysetting2.utils.SpUtil;

import java.util.ArrayList;
import java.util.List;

public class LockService extends Service {
    private static final String TAG = "LockService";
    private Mybind mybind = new Mybind();
    public LockService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
       // throw new UnsupportedOperationException("Not yet implemented");
        return mybind;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mybind.startQuerryCurrentPackageName();
    }

    public class Mybind extends Binder{
        public void startQuerryCurrentPackageName(){
            //开启一个线程来不停获取包名
            new Thread(new Mythread()).start();
        }
    }

    public class Mythread implements Runnable {

        @Override
        public void run() {
            while (true){
                try{
                    Thread.sleep(500);
                    Message message = new Message();
                    message.what = 1;
                    handler.sendMessage(message);


                }catch (Exception e){

                }
            }
        }
    }

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            //
            if(msg.what == 1){
                SpUtil spUtil = SpUtil.getInstance();
                spUtil.init(LockService.this);
                String lockAppPackageName = spUtil.getString("lockAppPackageName");
                ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
                String packageName = getCurrentPackageName(LockService.this, activityManager);
                boolean isLock = LockUtil.isLock(packageName);

                if (!TextUtils.isEmpty(lockAppPackageName)) {
                    if (!TextUtils.isEmpty(packageName)) {
                        if (!lockAppPackageName.equals(packageName)) {
                            //再加多层判断，更加准确，如果返回了桌面,或者后台切换的时候
                            if (getHomes().contains(packageName) || packageName.contains("launcher") ) {
                                //这个包是自己不行，因为dialog界面packagename就是自己
                                if(!packageName.contains("securitysetting2")){
                                    //这是设置相关的代码，表示是否设置了不锁这个应用，可忽略
                                    if (!isLock) {
                                        //加锁
                                        LockUtil.setLock(lockAppPackageName,true);
                                    }
                                }
                            }
                        }
                    }
                }

                if(packageName != null && packageName != ""){
                    if(isLock){//表示这个包名上锁了，那么弹出dialog输入密码
                        spUtil.putString("lockAppPackageName",packageName);
                        Intent intent = new Intent(LockService.this, ScreetActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        LockService.this.startActivity(intent);
                    }


                }
            }
        }
    };


    public String getCurrentPackageName(Context context, ActivityManager activityManager){
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            List<ActivityManager.RunningTaskInfo> appTasks = activityManager.getRunningTasks(1);
            if (null != appTasks && !appTasks.isEmpty()) {
                return appTasks.get(0).topActivity.getPackageName();
            }
        } else {
            //5.0以后需要用这方法
            UsageStatsManager sUsageStatsManager = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
            long endTime = System.currentTimeMillis();
            long beginTime = endTime - 10000;
            String result = "";
            UsageEvents.Event event = new UsageEvents.Event();
            UsageEvents usageEvents = sUsageStatsManager.queryEvents(beginTime, endTime);
            while (usageEvents.hasNextEvent()) {
                usageEvents.getNextEvent(event);
                if (event.getEventType() == UsageEvents.Event.MOVE_TO_FOREGROUND) {
                    result = event.getPackageName();
                }
            }
            if (!android.text.TextUtils.isEmpty(result)) {
                return result;
            }
        }
        return "";
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy: LockService I will be destoryed");
        startService(new Intent(this,LockService.class));
        super.onDestroy();
    }

    /**
     * 获得属于桌面的应用的应用包名称
     */
    private List<String> getHomes() {
        List<String> names = new ArrayList<>();
        PackageManager packageManager = this.getPackageManager();
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        List<ResolveInfo> resolveInfo = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        for (ResolveInfo ri : resolveInfo) {
            names.add(ri.activityInfo.packageName);
        }
        return names;
    }

}
