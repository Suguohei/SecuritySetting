package com.yhwxd.suguoqing.securitysetting2.adapter;

import android.content.Context;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yhwxd.suguoqing.securitysetting2.R;
import com.yhwxd.suguoqing.securitysetting2.bean.AppInfo;

import java.util.List;

public class AppAdapter extends RecyclerView.Adapter<AppAdapter.ViewHolder> {
    private static final String TAG = "AppAdapter";

    private List<AppInfo> data;
    private Context context;
    private onItemClickListener listener;
   // private View.OnClickListener moreListener;
    private onMoreClickListner moreListener;

    public AppAdapter(List<AppInfo> data, Context context) {
        super();
        this.data = data;
        this.context = context;
    }

    public void setListener(onItemClickListener listener,onMoreClickListner moreListener) {
        this.listener = listener;
        this.moreListener = moreListener;
    }

    public interface onItemClickListener {
        void onItemClick(String packageName);
    }

    public interface onMoreClickListner{
        void onMoreClick(String packageName);
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if(context == null){
            context = viewGroup.getContext();
        }
        View view = LayoutInflater.from(context).inflate(R.layout.app_item,viewGroup,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int i) {

        viewHolder.icon.setImageBitmap(data.get(i).getIcon());
        viewHolder.name.setText(data.get(i).getName());
        viewHolder.isHide.setChecked(data.get(i).isHide());
        viewHolder.isHide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String packageName = data.get(i).getPackageName();
                listener.onItemClick(packageName);
            }
        });
        viewHolder.more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String packageName = data.get(i).getPackageName();
                moreListener.onMoreClick(packageName);
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView icon;
        TextView name;
        CheckBox isHide;
        ImageButton more;
        View item;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            item = itemView;
            icon = itemView.findViewById(R.id.icon);
            name = itemView.findViewById(R.id.name);
            isHide = itemView.findViewById(R.id.checkbox);
            more = itemView.findViewById(R.id.more);
        }
    }
}
