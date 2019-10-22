package com.yhwxd.suguoqing.securitysetting2.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.yhwxd.suguoqing.securitysetting2.R;
import com.yhwxd.suguoqing.securitysetting2.bean.DeviceAdmin;
import com.yhwxd.suguoqing.securitysetting2.utils.DeviceAdminUtil;
import com.yhwxd.suguoqing.securitysetting2.utils.DeviceOwnerUtil;
import com.yhwxd.suguoqing.securitysetting2.utils.SpUtil;

public class AdminFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener {
    private static final int RESULT_ENABLE = 1;
    private static final String TAG = "AdminFragment";

    private final static String KEY_ADMIN = "get_admin";
    private final static String KEY_REMOVE_ADMIN = "remove_admin";
    private final static String KEY_LOCK_SCREEN = "lockSceen";
    private final static String KEY_FORBID_CAMERA = "forbid_camera";
    private final static String KEY_BLUE_TEE = "blue_tee";
    private final static String KEY_WIFI = "wifi";
    private final static String KEY_DATA = "data";

    private Preference get_admin;
    private Preference remove_admin;
    private Preference lockScreen;
    private SwitchPreference forbid_camera;
    private SwitchPreference blue_tee;
    private SwitchPreference wifi;
    private SwitchPreference data;

    private boolean isGetAdmin;


    private DevicePolicyManager devicePolicyManager;
    private ComponentName admin;
    private ContentResolver resolver;
    private Activity activity;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.admin_premission);
        activity = getActivity();
        devicePolicyManager = (DevicePolicyManager) getActivity().getSystemService(Context.DEVICE_POLICY_SERVICE);
        admin = new ComponentName(this.getActivity(), DeviceAdmin.class);
        resolver = getActivity().getContentResolver();

        boolean isGetDeviceOwner = DeviceOwnerUtil.isDeviceOwnerApp(devicePolicyManager,"com.yhwxd.suguoqing.securitysetting2");
        Log.d(TAG, "onCreate: isGetDeviceOwner"+isGetDeviceOwner);
        if(isGetDeviceOwner){
            isGetAdmin = true;
        }else {
            isGetAdmin = DeviceAdminUtil.isGetAdmin(devicePolicyManager,admin);
        }

        initPreference();
    }

    private void initPreference() {
        get_admin = findPreference("get_admin");
        remove_admin = findPreference("remove_admin");
        lockScreen = findPreference("lockSceen");
        forbid_camera = (SwitchPreference) findPreference("forbid_camera");
        blue_tee = (SwitchPreference) findPreference("blue_tee");
        wifi = (SwitchPreference) findPreference("wifi");
        data = (SwitchPreference) findPreference("data");

        //-----------------------get-admin----------------------
        get_admin.setEnabled(!isGetAdmin);
        get_admin.setSummary(isGetAdmin ? "已经获取管理员" : "没有获取管理员");
        get_admin.setOnPreferenceClickListener(listener);

        //------------------------remove_admin-------------------
        remove_admin.setEnabled(isGetAdmin);
        remove_admin.setOnPreferenceClickListener(listener);

        //------------------------lockScreen-----------------------
        lockScreen.setEnabled(isGetAdmin);
        lockScreen.setOnPreferenceClickListener(listener);

        //-------------------------forbid_camera---------------------
        boolean isForbidCamera = DeviceAdminUtil.isOpenCamera(devicePolicyManager,admin);
        forbid_camera.setEnabled(isGetAdmin);
        forbid_camera.setChecked(isForbidCamera);
        forbid_camera.setSummary(isForbidCamera?R.string.camera_disable:R.string.camera_enable);
        forbid_camera.setOnPreferenceChangeListener(this);

        //-------------------------blue tee -------------------------------------
       // blue_tee.setEnabled(isGetAdmin);
        blue_tee.setSummary(DeviceAdminUtil.isOpenBlue() ? R.string.blue_tee_open:R.string.blue_tee_close);
        blue_tee.setChecked(DeviceAdminUtil.isOpenBlue());
        blue_tee.setOnPreferenceChangeListener(this);

        //--------------------------wifi--------------------------------
        //wifi.setEnabled(isGetAdmin);
        WifiManager manager = (WifiManager) activity.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        wifi.setSummary(DeviceAdminUtil.isOpenWifi(manager) ? R.string.wifi_open:R.string.wifi_close);
        wifi.setChecked(DeviceAdminUtil.isOpenWifi(manager));
        wifi.setOnPreferenceChangeListener(this);

        //-------------------------data---------------
        //data.setEnabled(isGetAdmin);
        TelephonyManager telephonyManager = (TelephonyManager) activity.getSystemService(Context.TELEPHONY_SERVICE);
        data.setChecked(DeviceAdminUtil.isOpenData(telephonyManager));
        data.setSummary(DeviceAdminUtil.isOpenData(telephonyManager)?"数据流量已经打开":"数据流量已经关闭");
        data.setOnPreferenceChangeListener(this);


    }



    Preference.OnPreferenceClickListener listener = new Preference.OnPreferenceClickListener() {
        @Override
        public boolean onPreferenceClick(Preference preference) {
            if (KEY_ADMIN.equals(preference.getKey())){
                Log.d(TAG, "onPreferenceClick: "+preference.getKey());
                getAdmin(admin);
                setAdminDisable();
               // DeviceAdminUtil.getAdmin(admin,getActivity());
            }else if (KEY_REMOVE_ADMIN.equals(preference.getKey())){
                Log.d(TAG, "onPreferenceClick: remove admin");
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                AlertDialog dialog = builder.setTitle(R.string.remove_admin)
                        .setMessage(R.string.ensure_cancel_admin)
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(activity, "this is cancel admin", Toast.LENGTH_SHORT).show();
                                setAdminEnable();//ui
                                DeviceAdminUtil.cancelAdmin(devicePolicyManager,admin);
                            }
                        })
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .create();
                dialog.setCanceledOnTouchOutside(true);
                dialog.show();
            }else if (KEY_LOCK_SCREEN.equals(preference.getKey())){
                DeviceAdminUtil.lockNow(devicePolicyManager);
            }

            return true;
        }
    };

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if(KEY_FORBID_CAMERA.equals(preference.getKey())){

            DeviceAdminUtil.setCameraDisabled(devicePolicyManager,admin,(boolean) newValue);
            forbid_camera.setSummary((boolean) newValue ? R.string.camera_disable:R.string.camera_enable);

        }else if (KEY_BLUE_TEE.equals(preference.getKey())){
            DeviceAdminUtil.setBlue((Boolean) newValue);
            blue_tee.setSummary((Boolean) newValue ? R.string.blue_tee_open:R.string.blue_tee_close);

        }else if(KEY_WIFI.equals(preference.getKey())){
            WifiManager wifiManager = (WifiManager) activity.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            DeviceAdminUtil.setWifi(wifiManager,(Boolean) newValue);
            wifi.setSummary((Boolean) newValue? R.string.wifi_open:R.string.wifi_close);
        }else if (KEY_DATA.equals(preference.getKey())){
            /*TelephonyManager telephonyManager = (TelephonyManager) activity.getSystemService(Context.TELEPHONY_SERVICE);
            DeviceAdminUtil.setData(telephonyManager,(Boolean) newValue);
            data.setSummary((Boolean) newValue?"数据流量已经打开":"数据流量已经关闭");*/
            if(ActivityCompat.checkSelfPermission(activity, Manifest.permission.MODIFY_PHONE_STATE)
                    != PackageManager.PERMISSION_GRANTED){
                
                ActivityCompat.requestPermissions(activity,
                        new String[]{Manifest.permission.MODIFY_PHONE_STATE},0);
                Log.d(TAG, "onPreferenceChange: suguoqing");
            }else {
                Log.d(TAG, "onPreferenceChange: "+newValue);
                tempNewValue = (boolean) newValue;
                changeData((Boolean) newValue);
            }
        }
        return true;
    }

    private void changeData(boolean newValue) {
        TelephonyManager telephonyManager = (TelephonyManager) activity.getSystemService(Context.TELEPHONY_SERVICE);
            DeviceAdminUtil.setData(telephonyManager,newValue);
            data.setSummary((Boolean) newValue?"数据流量已经打开":"数据流量已经关闭");
    }

    private boolean tempNewValue;
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 0:
                if(grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){

                    changeData(tempNewValue);
                }else {
                    Toast.makeText(activity, "没有权限", Toast.LENGTH_SHORT).show();
                }
                

                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        Log.d(TAG, "onActivityResult: requestCode:"+requestCode+"----resultCode:"+resultCode);
        switch (requestCode) {
            case RESULT_ENABLE:
                if (resultCode == Activity.RESULT_OK) {
                    Log.v("DeviceEnable", "deviceAdmin:enable");

                } else {
                    Log.v("DeviceEnable", "deviceAdmin:disable");

                }
                break;

        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    /*
     * 激活管理员
     * */
    public void getAdmin(ComponentName mDeviceComponentName){
        Intent intent = new Intent(
                DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN,
                mDeviceComponentName);
        intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,
                "这里可以输入一些额外的说明,比如提示用户什么的");
        startActivityForResult(intent, RESULT_ENABLE);
    }

    /*
     * set admin enable
     * */
    public void setAdminEnable(){

        get_admin.setEnabled(true);
        get_admin.setSummary(R.string.not_get_admin);

        forbid_camera.setEnabled(false);//deng deng .....
        remove_admin.setEnabled(false);
        lockScreen.setEnabled(false);
        //wifi.setEnabled(false);
        //blue_tee.setEnabled(false);
      //  reset_screet.setEnabled(false);
      //  lock_info.setEnabled(false);
     //   uninstall.setEnabled(false);
      //  reset_uninstall.setEnabled(false);
       // silence.setEnabled(false);
      //  capScreen.setEnabled(false);
     //   statusbar.setEnabled(false);
      //  reboot.setEnabled(false);
    }

    /*
     * set admin disable
     * */
    public void setAdminDisable(){

        get_admin.setEnabled(false);
        get_admin.setSummary(R.string.get_admin);

        forbid_camera.setEnabled(true);
        remove_admin.setEnabled(true);
        lockScreen.setEnabled(true);
      //  wifi.setEnabled(true);
      //  blue_tee.setEnabled(true);
      //  reset_screet.setEnabled(true);
      //  lock_info.setEnabled(true);
       // uninstall.setEnabled(true);
       // reset_uninstall.setEnabled(true);
      //  silence.setEnabled(true);
       // capScreen.setEnabled(true);
       // statusbar.setEnabled(true);
       // reboot.setEnabled(true);
        //deng deng....
    }

}
