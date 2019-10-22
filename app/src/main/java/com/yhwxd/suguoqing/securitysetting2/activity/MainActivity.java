package com.yhwxd.suguoqing.securitysetting2.activity;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.yhwxd.suguoqing.securitysetting2.R;
import com.yhwxd.suguoqing.securitysetting2.adapter.MainMenuAdapter;
import com.yhwxd.suguoqing.securitysetting2.bean.DeviceAdmin;
import com.yhwxd.suguoqing.securitysetting2.bean.MainMenuItem;
import com.yhwxd.suguoqing.securitysetting2.utils.LockUtil;
import com.yhwxd.suguoqing.securitysetting2.utils.SpUtil;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private final static int RESULT_ACTION_USAGE_ACCESS_SETTINGS = 1;

    private RecyclerView recyclerView;
    private List<MainMenuItem> data = new ArrayList<>();
    private MainMenuAdapter.OnItemClickListener listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initData();
        initListener();
        recyclerView = findViewById(R.id.recycleView);
        MainMenuAdapter adapter = new MainMenuAdapter(data,this);
        adapter.setListener(listener);
        GridLayoutManager manager = new GridLayoutManager(MainActivity.this,2);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);
    }

    private void initListener() {
        listener = new MainMenuAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int postion) {
                //Toast.makeText(MainActivity.this, "id is:"+view.getId()+"--postion:"+postion, Toast.LENGTH_SHORT).show();
                switch (postion){
                    case 0:
                        Intent admin = new Intent("com.yhwxd.suguoqing.securitysetting2.admin");
                        startActivity(admin);
                        break;
                    case 1:
                        Intent owner = new Intent("com.yhwxd.suguoqing.securitysetting2.owner");
                        startActivity(owner);
                        break;

                    case 2:
                        showDialog();
                        break;
                    case 3:
                        String command = "dpm set-device-owner com.yhwxd.suguoqing.securitysetting2/com.yhwxd.suguoqing.securitysetting2.bean.DeviceAdmin";

                        break;
                    case 4:
                        reset();
                        break;
                    case 5:
                        Intent info = new Intent("com.yhwxd.suguoqing.securitysetting2.info");
                        startActivity(info);
                        break;
                        default:
                            break;

                }
            }
        };

    }


    private void initData() {
        String[] menuNames = getResources().getStringArray(R.array.menu_name);
        int[] icons = new int[]{R.mipmap.admin,R.mipmap.manager,R.mipmap.apps,
                R.mipmap.password,R.mipmap.backup,R.mipmap.info,
                };

        for(int i = 0 ;i < menuNames.length;i++){
            MainMenuItem mainMenuItem = new MainMenuItem();
            mainMenuItem.setItemImg(BitmapFactory.decodeResource(getResources(),icons[i]));
            mainMenuItem.setItemText(menuNames[i]);
            Log.d(TAG, "datainit: "+mainMenuItem);
            data.add(mainMenuItem);
        }
    }

    private void showDialog() {
        Log.d(TAG, "showDialog: suguoqing"+LockUtil.isStatAccessPermissionSet(MainActivity.this));
        if(!LockUtil.isStatAccessPermissionSet(MainActivity.this)
                && LockUtil.isNoOption(MainActivity.this)){
            Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
            startActivityForResult(intent, RESULT_ACTION_USAGE_ACCESS_SETTINGS);

        }else{
            //Toast.makeText(this, "you has been get permission,you should goto applist activity", Toast.LENGTH_SHORT).show();
            gotoScreetActivity();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == RESULT_ACTION_USAGE_ACCESS_SETTINGS){
            if(LockUtil.isStatAccessPermissionSet(MainActivity.this)){
                gotoScreetActivity();
            }else {
                Toast.makeText(this, "no permission", Toast.LENGTH_SHORT).show();
            }

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void gotoScreetActivity() {
        Intent intent = new Intent("com.yhwxd.suguoqing.securitysetting2.screet");
        SpUtil spUtil = SpUtil.getInstance();
        spUtil.init(this);
        spUtil.putString("lockAppPackageName","");
        startActivity(intent);
        //Toast.makeText(this, "i can go to app list activity", Toast.LENGTH_SHORT).show();
    }


    private void reset(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        AlertDialog alertDialog = builder.setTitle(R.string.reset)
                .setMessage(R.string.tips)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DevicePolicyManager manager = (DevicePolicyManager) getSystemService(DEVICE_POLICY_SERVICE);
                        ComponentName componentName = new ComponentName(MainActivity.this, DeviceAdmin.class);
                        boolean active = manager.isAdminActive(componentName);
                        if(active){
                            manager.wipeData(0);
                        }else{
                            Toast.makeText(MainActivity.this, R.string.not_get_admin, Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNeutralButton(android.R.string.cancel, null)
                .create();

        alertDialog.show();

    }

}
