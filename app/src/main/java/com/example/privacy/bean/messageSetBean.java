package com.example.privacy.bean;

public class messageSetBean {
    private String SetName;
    private int SetId;
    private int resId; // 图标资源id

    public messageSetBean(String setName, int setId, int resId) {
        SetName = setName;
        SetId = setId;
        this.resId = resId;
    }

    public String getSetName() {
        return SetName;
    }

    public void setSetName(String setName) {
        SetName = setName;
    }

    public int getSetId() {
        return SetId;
    }

    public void setSetId(int setId) {
        SetId = setId;
    }

    public int getResId() {
        return resId;
    }

    public void setResId(int resId) {
        this.resId = resId;
    }
}
