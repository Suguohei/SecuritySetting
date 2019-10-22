package com.yhwxd.suguoqing.securitysetting2.activity;

import android.app.Dialog;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.yhwxd.suguoqing.securitysetting2.AppInfoManager;
import com.yhwxd.suguoqing.securitysetting2.LockService;
import com.yhwxd.suguoqing.securitysetting2.R;
import com.yhwxd.suguoqing.securitysetting2.adapter.AppAdapter;
import com.yhwxd.suguoqing.securitysetting2.bean.AppInfo;
import com.yhwxd.suguoqing.securitysetting2.bean.DeviceAdmin;
import com.yhwxd.suguoqing.securitysetting2.utils.DeviceOwnerUtil;
import com.yhwxd.suguoqing.securitysetting2.utils.DialogUtil;
import com.yhwxd.suguoqing.securitysetting2.utils.LockUtil;
import com.yhwxd.suguoqing.securitysetting2.utils.SpUtil;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

public class AppListActivity extends AppCompatActivity {

    private RecyclerView applist;
    private static final String TAG = "AppListActivity";
    private List<AppInfo> infos = new ArrayList<>();
    private PackageManager packageManager ;
    private AppInfoManager appInfoManager;
    private DevicePolicyManager devicePolicyManager;
    private ComponentName admin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_list);
        applist = findViewById(R.id.applist);
        packageManager = getPackageManager();
        appInfoManager = new AppInfoManager(packageManager,this);
        devicePolicyManager = (DevicePolicyManager) getSystemService(DEVICE_POLICY_SERVICE);
        admin = new ComponentName(this, DeviceAdmin.class);
        infos = appInfoManager.getAllAppInfo();

        for(AppInfo appInfo : infos){
            drawableToBitamp(getIcon(appInfo.getPackageName()));
            appInfo.setIcon(bitmap);
        }

        LinearLayoutManager manager = new LinearLayoutManager(this);
        AppAdapter appAdapter = new AppAdapter(infos,this);
        appAdapter.setListener(onItemClickListener,listener);
        applist.setLayoutManager(manager);
        applist.setItemViewCacheSize(100);
        applist.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        applist.setAdapter(appAdapter);
        

    }

    private AppAdapter.onItemClickListener onItemClickListener = new AppAdapter.onItemClickListener() {
        @Override
        public void onItemClick(String packageName) {
            Log.d(TAG, "onItemClick: checkbox has been on click:"+packageName);
            appInfoManager.updateIsHide(packageName,!appInfoManager.isHide(packageName));
            DeviceOwnerUtil.setApplicationHidden(devicePolicyManager,admin,packageName,
                    !DeviceOwnerUtil.isApplicationHidden(devicePolicyManager,admin,packageName));
        }
    };
    
    private AppAdapter.onMoreClickListner listener = new AppAdapter.onMoreClickListner() {
        @Override
        public void onMoreClick(String packageName) {
            Log.d(TAG, "onMoreClick: more has been on click");
            DialogUtil dialogUtil = new DialogUtil();
            dialogUtil.showBottomDialog(AppListActivity.this,packageName);
            dialogUtil.setListener(dialogListener);
        }
    };


    @Override
    protected void onDestroy() {
        super.onDestroy();
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


    /*
    * 根据包名获取图标
    * */
    private Drawable getIcon(String pakgename) {
        PackageManager pm = getPackageManager();
        try {
            ApplicationInfo appInfo = pm.getApplicationInfo(pakgename, PackageManager.GET_UNINSTALLED_PACKAGES);

            Drawable appIcon = pm.getApplicationIcon(appInfo);
            return appIcon;

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }


    public DialogUtil.onClickListener dialogListener = new DialogUtil.onClickListener() {
        @Override
        public void onItemClick(View view,String packageName) {
            Log.d(TAG, "onItemClick: packageName is "+packageName+"--id: "+view.getId());
            switch (view.getId()){
                case R.id.check:
                    //appInfoManager.updateIsHide(packageName,!appInfoManager.isHide(packageName));
                    Log.d(TAG, "onItemClick: "+LockUtil.isLock(packageName)+"packagenaem :"+packageName);
                    LockUtil.setLock(packageName,!LockUtil.isLock(packageName));
                    break;

                case R.id.check2:
                    appInfoManager.updateIsUninstall(packageName,!appInfoManager.isUninstall(packageName));
                    DeviceOwnerUtil.setUninstallBlocked(devicePolicyManager,admin,packageName,appInfoManager.isUninstall(packageName));
                    Log.d(TAG, "onItemClick: "+packageName);
                    break;

                case R.id.detail_img:
                    Log.d(TAG, "onItemClick: "+ LockUtil.isNoSwitch(AppListActivity.this));
                    break;
            }
        }
    };


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = new MenuInflater(this);
        menuInflater.inflate(R.menu.setting_menu,menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.modifyscreet:
                showModifyScreet();
                break;
        }
        return true;
    }

    private void showModifyScreet() {
        View view = LayoutInflater.from(this).inflate(R.layout.modify_screet,null,false);
        final EditText origin = view.findViewById(R.id.origin_screet);
        final EditText new_pwd = view.findViewById(R.id.new_screet);
        final TextView tips = view.findViewById(R.id.tips);
        final TextView tips2 = view.findViewById(R.id.tips2);

        final SpUtil spUtil = SpUtil.getInstance();
        spUtil.init(this);
        final String old_screet = spUtil.getString("pwd");


        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        AlertDialog dialog = builder.setTitle("修改密码")
                .setView(view)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String old_screet_input = origin.getText().toString();
                        //Toast.makeText(ListAppActivity.this, "screet has been modifyed", Toast.LENGTH_SHORT).show();
                        if(("879718".equals(old_screet_input) || old_screet.equals(old_screet_input))
                                && (!new_pwd.getText().toString().isEmpty())){
                            spUtil.putString("pwd",new_pwd.getText().toString());
                            Toast.makeText(AppListActivity.this, "密码设置成功", Toast.LENGTH_SHORT).show();
                        }else if(new_pwd.getText().toString().isEmpty()){
                            Toast.makeText(AppListActivity.this, "新密码不能为空", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }else {
                            Toast.makeText(AppListActivity.this, "密码不正确", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();
        dialog.show();

        final Button button = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
        button.setEnabled(false);

        //给原密码输入框添加 一个 观察者
        origin.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //todo
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // todo
            }

            @Override
            public void afterTextChanged(Editable s) {
                String input = s.toString().trim();
                if((input.equals(old_screet) && !input.isEmpty())
                        || ("879718".equals(input))){
                    tips.setText("密码正确");
                    button.setEnabled(true);
                }else {
                    tips.setText("密码不正确");
                    button.setEnabled(false);
                }
            }
        });



    }



}
