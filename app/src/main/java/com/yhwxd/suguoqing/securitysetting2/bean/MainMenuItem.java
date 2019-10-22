package com.yhwxd.suguoqing.securitysetting2.bean;

import android.graphics.Bitmap;


public class MainMenuItem {
    Bitmap itemImg;
    String itemText;

    public Bitmap getItemImg() {
        return itemImg;
    }

    public void setItemImg(Bitmap itemImg) {
        this.itemImg = itemImg;
    }

    public String getItemText() {
        return itemText;
    }

    public void setItemText(String itemText) {
        this.itemText = itemText;
    }

    @Override
    public String toString() {
        return "MainMenuItem{" +
                "itemImg=" + itemImg +
                ", itemText='" + itemText + '\'' +
                '}';
    }
}
