package com.example.privacy;

import java.util.ArrayList;

public class album {
    private ArrayList<PhotoItem> photoSet;
    private String album_name;  // 相册名字
    private int resId;    // 资源id
    private int albumId;      // 相册id

    public album(ArrayList<PhotoItem> photoItems, String str, int resId, int albumId) {
        this.photoSet = photoItems;
        this.album_name = str;
        this.resId = resId;
        this.albumId = albumId;
    }

    public void addPhotoItem(PhotoItem item) { // 添加照片的路径与名字
        photoSet.add(item);
    }

    public void setPhotoSet(ArrayList<PhotoItem> photoSet) {
        this.photoSet = photoSet;
    }

    public ArrayList<PhotoItem> getPhotoSet() {
        return photoSet;
    }

    public int getPhotoSize(){
        return photoSet.size();
    }

    public String getAlbum_name() {
        return album_name;
    }

    public int getAlbumId() {
        return albumId;
    }

    public void setAlbumId(int albumId) {
        this.albumId = albumId;
    }

    public int getResId() {
        return resId;
    }

    public void setResId(int resId) {
        this.resId = resId;
    }
}
