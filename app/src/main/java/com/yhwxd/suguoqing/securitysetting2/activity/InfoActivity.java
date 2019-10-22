package com.yhwxd.suguoqing.securitysetting2.activity;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.widget.TextView;
import android.widget.Toast;

import com.yhwxd.suguoqing.securitysetting2.R;
import com.yhwxd.suguoqing.securitysetting2.utils.SystemUtils;

public class InfoActivity extends AppCompatActivity {

    private TextView model;
    private TextView imei;
    private TextView version;
    private TextView version_soft;
    private TextView blue_address;
    private TextView wifi_address;
    private TextView number;
    private TextView rom;
    private TextView ram;
    private TextView level;

    String Model = "";
    String Imei = "";
    String soft_version = "";
    String phone_number = "";
    static int levels;
    TelephonyManager telephonyManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        initView();
    }

    private void initView() {
        model = findViewById(R.id.model);
        imei = findViewById(R.id.imei);
        version = findViewById(R.id.version);
        version_soft = findViewById(R.id.version_soft);
        blue_address = findViewById(R.id.blue_address);
        wifi_address = findViewById(R.id.wifi_address);
        number = findViewById(R.id.number);
        rom = findViewById(R.id.rom);
        ram = findViewById(R.id.ram);
        level = findViewById(R.id.level);

        telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);

        Model = Build.MODEL;
        model.setText("机器型号 :" + Model);

        //Imei = getImei();
        checkPremission();
        imei.setText("IMEI :"+Imei);

        version.setText("操作系统 Android："+Build.VERSION.RELEASE);

        version_soft.setText("软件版本号："+soft_version);

        blue_address.setText("蓝牙地址 :"+ SystemUtils.GetLocalMacAddress());

        wifi_address.setText("wifi MAC地址 :"+SystemUtils.getWifiMacAddress(this)[0]);

        number.setText("电话号码："+phone_number);

        long TotalMemory = SystemUtils.getTotalMemory(this);
        long AvailMemory = SystemUtils.getAvailMemory(this);
        ram.setText("RAM:" + SystemUtils.formatSize(AvailMemory) + "/" + SystemUtils.formatSize(TotalMemory));


        long[] RomMemroy = SystemUtils.getRomMemroy();
        long totalyRom = SystemUtils.getTotalInternalMemorySize();
        rom.setText("ROM:" + SystemUtils.formatSize(RomMemroy[1]) + "/" + SystemUtils.formatSize(totalyRom));

        registerReceiver(batteryReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        level.setText("当前电量："+levels);


        /**
         * 获取数据连接状态
         *
         * DATA_CONNECTED 数据连接状态：已连接
         * DATA_CONNECTING 数据连接状态：正在连接
         * DATA_DISCONNECTED 数据连接状态：断开
         * DATA_SUSPENDED 数据连接状态：暂停
         */
        // Button button = findViewById(R.id.data);
        /*button.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                if(telephonyManager.getDataState() == TelephonyManager.DATA_DISCONNECTED){
                    telephonyManager.setDataEnabled(true);
                }else {
                    telephonyManager.setDataEnabled(false);
                }
            }
        });*/



    }

    public void checkPremission(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_PHONE_STATE}, 1);
        } else {
            Imei = telephonyManager.getDeviceId();// api 26 之前用getDeviceId，之后用getImei
            if(Imei == null){
                Imei = "设备不可用";
            }
            soft_version = telephonyManager.getDeviceSoftwareVersion();
            if(soft_version == null){
                soft_version = "软件版本不可用";
            }

            phone_number = telephonyManager.getLine1Number();
            if(phone_number == null){
                phone_number = "无";

            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 1:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    //Imei = getImei();
                    checkPremission();
                }else {
                    Toast.makeText(this, "没有权限", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }

    /**
     * 电池电量
     */
    public static BroadcastReceiver batteryReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            levels = intent.getIntExtra("level", 0);
            //  levels加%就是当前电量了

        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(batteryReceiver);
    }
}
