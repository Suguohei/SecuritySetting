package com.yhwxd.suguoqing.securitysetting2.activity;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.PreferenceFragment;
import android.preference.Preference;
import android.preference.SwitchPreference;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.yhwxd.suguoqing.securitysetting2.R;
import com.yhwxd.suguoqing.securitysetting2.bean.DeviceAdmin;
import com.yhwxd.suguoqing.securitysetting2.utils.DeviceOwnerUtil;
import com.yhwxd.suguoqing.securitysetting2.utils.SpUtil;

public class OwnerFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener {

    private static final String TAG = "OwnerFragment";
    private Activity activity;
    private final static String KEY_RESET_SCREET = "reset_screet";
    private final static String KEY_LOCK_INFO = "lockInfo";
    private final static String KEY_SILENCE = "silence";
    private final static String KEY_CAP_SCREEN = "capScreen";
    private final static String KEY_STATUS_BAR = "statusbar";
    private final static String KEY_REBOOT = "reboot";

    private EditTextPreference reset_screet;
    private EditTextPreference lock_info;
    private SwitchPreference silence;
    private SwitchPreference capScreen;
    private SwitchPreference statusbar;
    private Preference reboot;

    private DevicePolicyManager devicePolicyManager;
    private ComponentName componentName;
    private ContentResolver resolver;
    private SpUtil spUtil;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
        addPreferencesFromResource(R.xml.owner_premission);
        devicePolicyManager = (DevicePolicyManager) getActivity().getSystemService(Context.DEVICE_POLICY_SERVICE);
        componentName = new ComponentName(this.getActivity(), DeviceAdmin.class);
        resolver = getActivity().getContentResolver();
        spUtil = SpUtil.getInstance();
        spUtil.init(activity);
        initPreference();
    }

    private void initPreference() {
        reset_screet = (EditTextPreference) findPreference("reset_screet");
        lock_info = (EditTextPreference) findPreference("lockInfo");
        silence = (SwitchPreference) findPreference("silence");
        capScreen = (SwitchPreference) findPreference("capScreen");
        statusbar = (SwitchPreference) findPreference("statusbar");
        reboot = findPreference("reboot");

        //---------------------------reset screet---------------------------
        reset_screet.setOnPreferenceChangeListener(this);

        //----------------------------lock info-------------------------
        lock_info.setOnPreferenceChangeListener(this);

        //----------------------------silence ---------------------------
        boolean isSilence = DeviceOwnerUtil.isMasterVolumeMuted(devicePolicyManager,componentName);
        silence.setChecked(isSilence);
        silence.setOnPreferenceChangeListener(this);

        //----------------------------cap screen----------------------------
        boolean isCapScreen = DeviceOwnerUtil.getScreenCaptureDisabled(devicePolicyManager,componentName);
        capScreen.setChecked(isCapScreen);
        capScreen.setOnPreferenceChangeListener(this);

        //-----------------------------status bar ---------------------------
        boolean flag = spUtil.getBoolean("getStatusbar");
        Log.d(TAG, "initPreference: "+flag);
        statusbar.setChecked(flag);
        statusbar.setOnPreferenceChangeListener(this);

        //------------------------------reboot -----------------------------
        reboot.setOnPreferenceClickListener(listener);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        String key = preference.getKey();
        Log.d(TAG, "onPreferenceChange: preference getkey is "+key+",newValue :"+newValue);
        final ContentResolver resolver = getActivity().getContentResolver();
        if (KEY_RESET_SCREET.equals(key)){
            //在Android n 开始，devices admin 只能在没设置密码的情况下，设置密码，但是devices owner可以重置密码，即使一开始也有密码
            //在Android o 就要用resetPasswordWithToken 这个方法了
            if(DeviceOwnerUtil.resetPassword(devicePolicyManager,(String)newValue)){
                Log.d(TAG, "onPreferenceChange: set screet success");
            }else{
                Log.d(TAG, "onPreferenceChange: set screet failed");
            }
        }else if(KEY_LOCK_INFO.equals(key)){

            DeviceOwnerUtil.setDeviceOwnerLockScreenInfo(devicePolicyManager,componentName, (String) newValue);

        }else if(KEY_SILENCE.equals(key)){

            boolean isSilence = DeviceOwnerUtil.isMasterVolumeMuted(devicePolicyManager,componentName);
            DeviceOwnerUtil.setMasterVolumeMuted(devicePolicyManager,componentName,!isSilence);
            Toast.makeText(activity, "设置成功", Toast.LENGTH_SHORT).show();

        }else if (KEY_CAP_SCREEN.equals(key)){

            boolean isCapScreen = DeviceOwnerUtil.getScreenCaptureDisabled(devicePolicyManager,componentName);
            DeviceOwnerUtil.setScreenCaptureDisabled(devicePolicyManager,componentName,isCapScreen);
            Toast.makeText(activity, "设置成功", Toast.LENGTH_SHORT).show();
        }else if (KEY_STATUS_BAR.equals(key)){
            boolean flag = spUtil.getBoolean("getStatusbar");
            Log.d(TAG, "onPreferenceChange: "+flag);
            if(flag){
                DeviceOwnerUtil.setStatusBarDisabled(devicePolicyManager,componentName,false);
                spUtil.putBoolean("getStatusbar",false);
            }else {
                DeviceOwnerUtil.setStatusBarDisabled(devicePolicyManager,componentName,true);
                spUtil.putBoolean("getStatusbar",true);
            }
        }

        return true;
    }

    Preference.OnPreferenceClickListener listener = new Preference.OnPreferenceClickListener(){

        @Override
        public boolean onPreferenceClick(Preference preference) {
            DeviceOwnerUtil.reboot(devicePolicyManager,componentName);
            return false;
        }
    };
}
