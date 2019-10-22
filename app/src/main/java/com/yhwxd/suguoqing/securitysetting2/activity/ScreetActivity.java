package com.yhwxd.suguoqing.securitysetting2.activity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.yhwxd.suguoqing.securitysetting2.AppListIntentService;
import com.yhwxd.suguoqing.securitysetting2.LockService;
import com.yhwxd.suguoqing.securitysetting2.R;
import com.yhwxd.suguoqing.securitysetting2.utils.DialogUtil;
import com.yhwxd.suguoqing.securitysetting2.utils.LockUtil;
import com.yhwxd.suguoqing.securitysetting2.utils.SpUtil;

public class ScreetActivity extends AppCompatActivity {

    private View view;
    private EditText editText;
    private int isFirst;
    private String pwd;
    private AlertDialog dialogNotFirst;
    private AlertDialog dialogFirst;
    private Dialog dialog_screet;
    private SpUtil spUtil;
    private String lockAppPackageName;
    private static final String TAG = "ScreetActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.screetbackground);
        //开启一个service来加载applist,为了避免耗时操作
        startService(new Intent(this, AppListIntentService.class));

        view = LayoutInflater.from(this).inflate(R.layout.activity_screet,null,false);
        editText = view.findViewById(R.id.screet);
        //判断是不是第一次
        spUtil = SpUtil.getInstance();
        spUtil.init(this);
        isFirst = spUtil.getInt("isFirst");
        pwd = spUtil.getString("pwd");
        lockAppPackageName = spUtil.getString("lockAppPackageName");

        Log.d(TAG, "onCreate: lockAppPackageNmae"+lockAppPackageName);


        Log.d(TAG, "onCreate: isFirst"+isFirst);
        if(isFirst == 0){
            createDialogFirst();
        }else{
           // createDialogNotFirst();
            createDialogNotFirst2();
        }

    }


    public void createDialogFirst(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        dialogFirst = builder.setTitle("提示：")
                .setMessage("默认密码是：879718，请保存好！")
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Settings.System.putInt(getContentResolver(),"First",1);
                        spUtil.putInt("isFirst",1);
                        gotoListActvity();

                    }
                })
                .setOnKeyListener(new DialogInterface.OnKeyListener() {
                    @Override
                    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                        dialog.dismiss();
                        finish();
                        return true;
                    }
                })
                .create();
        dialogFirst.setCanceledOnTouchOutside(false);
        dialogFirst.show();
    }

   /* public void createDialogNotFirst(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        dialogNotFirst = builder.setTitle("请输入密码:")
                .setView(view)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(("879718".equals(editText.getText().toString())
                                ||  (pwd != "" && editText.getText().toString().equals(pwd))) ){

                            if(lockAppPackageName == null || "".equals(lockAppPackageName)){
                                gotoListActvity();
                            }
                            LockUtil.setLock(lockAppPackageName,false);//确认密码后，设置为false，避免重复弹出
                            ScreetActivity.this.finish();
                            
                        }else {
                            Toast.makeText(ScreetActivity.this, "密码错误", Toast.LENGTH_SHORT).show();
                            if(lockAppPackageName == null || "".equals(lockAppPackageName)){
                                finish();
                            }else {
                                LockUtil.goHome(ScreetActivity.this);
                            }
                        }
                    }
                })
                .setOnKeyListener(new DialogInterface.OnKeyListener() {
                    @Override
                    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                        if(keyCode == KeyEvent.KEYCODE_BACK){
                            if(lockAppPackageName == null || "".equals(lockAppPackageName)){
                                dialog.dismiss();
                                finish();
                            }else{
                                LockUtil.goHome(ScreetActivity.this);
                            }
                        }
                        return false;
                    }
                })
                .create();

        dialogNotFirst.setCanceledOnTouchOutside(false);
        dialogNotFirst.show();
    }*/


    public void createDialogNotFirst2(){
        View view = LayoutInflater.from(this).inflate(R.layout.screet_dialog,null,false);
        TextView ok = view.findViewById(R.id.ok_screet);
        final EditText input = view.findViewById(R.id.input);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(("879718".equals(input.getText().toString())
                        ||  (!"".equals(pwd) && input.getText().toString().equals(pwd))) ){

                    if(lockAppPackageName == null || "".equals(lockAppPackageName)){
                        gotoListActvity();
                    }
                    LockUtil.setLock(lockAppPackageName,false);//确认密码后，设置为false，避免重复弹出
                    ScreetActivity.this.finish();

                }else {
                    Toast.makeText(ScreetActivity.this, "密码错误", Toast.LENGTH_SHORT).show();
                    if(lockAppPackageName == null || "".equals(lockAppPackageName)){
                        finish();
                    }else {
                        LockUtil.goHome(ScreetActivity.this);
                    }
                }
            }
        });

        dialog_screet = new Dialog(this,R.style.DialogScreet);
        dialog_screet.setContentView(view);
        //点击空白部分自动消失
        dialog_screet.setCanceledOnTouchOutside(false);
        dialog_screet.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if(keyCode == KeyEvent.KEYCODE_BACK){
                    if(lockAppPackageName == null || "".equals(lockAppPackageName)){
                        dialog.dismiss();
                        finish();
                    }else{
                        LockUtil.goHome(ScreetActivity.this);
                    }
                }
                return false;
            }
        });

        //获取窗体
        Window window = dialog_screet.getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.gravity = Gravity.CENTER;
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        window.setAttributes(params);
        dialog_screet.show();

    }

    private void gotoListActvity() {
        //Toast.makeText(this, "you can go to applistActivity", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this,AppListActivity.class);
        startActivity(intent);
        finish();
        Log.d(TAG, "gotoListActvity: you can go to app list activity");
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(dialogNotFirst != null ){
            dialogNotFirst.dismiss();
        }

        if(dialogFirst != null){
            dialogFirst.dismiss();
        }

        if(dialog_screet != null){
            dialog_screet.dismiss();
        }
    }
}
