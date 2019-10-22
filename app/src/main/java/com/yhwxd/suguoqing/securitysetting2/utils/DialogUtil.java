package com.yhwxd.suguoqing.securitysetting2.utils;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.yhwxd.suguoqing.securitysetting2.AppInfoManager;
import com.yhwxd.suguoqing.securitysetting2.R;
import com.yhwxd.suguoqing.securitysetting2.activity.ScreetActivity;

public class DialogUtil {

    private Context context;
    private Dialog dialog;
    private Dialog dialog_screet;
    private View view;
    private CheckBox checkBox1;
    private CheckBox checkBox2;
    private ImageView detail;
    private TextView cancel;
    private onClickListener listener;
    private static final String TAG = "DialogUtil";

    public void showBottomDialog(Context context, String packageName){
        this.context = context;
        //自定义dialog布局
        view = LayoutInflater.from(context).inflate(R.layout.dialog_bottom,null,false);
        initView(view,packageName);
        //自定义dialog显示风格
        dialog = new Dialog(context,R.style.DialogBottom);
        //设置view
        dialog.setContentView(view);
        //点击空白部分自动消失
        dialog.setCanceledOnTouchOutside(true);
        //获取窗体
        Window window = dialog.getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.gravity = Gravity.BOTTOM;
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        window.setAttributes(params);
        dialog.show();

    }

    private void initView(View view,final String packageName) {
        checkBox1 = view.findViewById(R.id.check);
        checkBox2 = view.findViewById(R.id.check2);
        detail = view.findViewById(R.id.detail_img);
        cancel = view.findViewById(R.id.cancel);


        PackageManager packageManager = context.getPackageManager();
        AppInfoManager appInfoManager = new AppInfoManager(packageManager,context);

        checkBox1.setChecked(LockUtil.isLock(packageName) ? true : false);
        checkBox2.setChecked(appInfoManager.isUninstall(packageName) ? true : false);

        checkBox1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(v,packageName);
            }
        });

        checkBox2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(v,packageName);
            }
        });

        detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(v,packageName);
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                close();
            }
        });

    }

    public void close(){
        if (dialog != null) {
            dialog.dismiss();
            dialog = null;
        }

    }

    public interface onClickListener{
        void onItemClick(View view,String packageName);
    }

    public void setListener(onClickListener listener){
        this.listener = listener;
    }


}
