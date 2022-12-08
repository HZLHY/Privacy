package com.example.privacy.bean;

public class encryptDbBean {
    private String imageUrl;
    private int classId;
    private String encryptUrl;

    public encryptDbBean(String imageUrl, int classId, String encryptUrl) {
        this.imageUrl = imageUrl;
        this.classId = classId;
        this.encryptUrl = encryptUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getClassId() {
        return classId;
    }

    public void setClassId(int classId) {
        this.classId = classId;
    }

    public String getEncryptUrl() {
        return encryptUrl;
    }

    public void setEncryptUrl(String encryptUrl) {
        this.encryptUrl = encryptUrl;
    }
}
