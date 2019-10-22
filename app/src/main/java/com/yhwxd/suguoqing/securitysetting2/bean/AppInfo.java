package com.yhwxd.suguoqing.securitysetting2.bean;

import android.content.pm.ApplicationInfo;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import org.litepal.crud.DataSupport;

public class AppInfo extends DataSupport implements Parcelable {
    private String packageName;
    private Bitmap icon;
    private ApplicationInfo applicationInfo;
    private String name;
    private boolean isHide;
    private boolean isLock;
    private boolean isUninstall;

    public AppInfo() {
    }


    protected AppInfo(Parcel in) {
        packageName = in.readString();
        icon = in.readParcelable(Bitmap.class.getClassLoader());
        applicationInfo = in.readParcelable(ApplicationInfo.class.getClassLoader());
        name = in.readString();
        isHide = in.readByte() != 0;
        isLock = in.readByte() != 0;
        isUninstall = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(packageName);
        dest.writeParcelable(icon, flags);
        dest.writeParcelable(applicationInfo, flags);
        dest.writeString(name);
        dest.writeByte((byte) (isHide ? 1 : 0));
        dest.writeByte((byte) (isLock ? 1 : 0));
        dest.writeByte((byte) (isUninstall ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<AppInfo> CREATOR = new Creator<AppInfo>() {
        @Override
        public AppInfo createFromParcel(Parcel in) {
            return new AppInfo(in);
        }

        @Override
        public AppInfo[] newArray(int size) {
            return new AppInfo[size];
        }
    };

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public Bitmap getIcon() {
        return icon;
    }

    public void setIcon(Bitmap icon) {
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isHide() {
        return isHide;
    }

    public void setHide(boolean hide) {
        isHide = hide;
    }

    public boolean isLock() {
        return isLock;
    }

    public void setLock(boolean lock) {
        isLock = lock;
    }

    public boolean isUninstall() {
        return isUninstall;
    }

    public void setUninstall(boolean uninstall) {
        isUninstall = uninstall;
    }

    public ApplicationInfo getApplicationInfo() {
        return applicationInfo;
    }

    public void setApplicationInfo(ApplicationInfo applicationInfo) {
        this.applicationInfo = applicationInfo;
    }
}
