package com.example.privacy;

import android.graphics.BitmapFactory;
import android.os.Parcel;
import android.os.Parcelable;

public class PhotoItem implements Parcelable {
    private String image_url;  // 图片绝对路径
    private String image_name;
    private int albumId;    // 所属相册Id

    public PhotoItem(String url, String name, int Id) {
        this.image_url = url;
        this.image_name = name;
        this.albumId = Id;
    }

    protected PhotoItem(Parcel in) {
        this.image_url = in.readString();
        this.image_name = in.readString();
        this.albumId = in.readInt();
    }

    public static final Creator<PhotoItem> CREATOR = new Creator<PhotoItem>() {
        @Override
        public PhotoItem createFromParcel(Parcel in) {
            return new PhotoItem(in);
        }

        @Override
        public PhotoItem[] newArray(int size) {
            return new PhotoItem[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(image_url);
        parcel.writeString(image_name);
        parcel.writeInt(albumId);
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String url) {
        this.image_url = url;
    }

    public String getImage_name() {
        return image_name;
    }

    public void setImage_name(String name) {
        this.image_name = name;
    }

    public int getAlbumId() {
        return albumId;
    }

    public void setAlbumId(int albumId) {
        this.albumId = albumId;
    }

    public int getWidth() {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 1;
        options.inJustDecodeBounds = false;
        BitmapFactory.decodeFile(getImage_url(), options);
        return options.outWidth;
    }

    public int getHeight() {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 1;
        options.inJustDecodeBounds = false;
        BitmapFactory.decodeFile(getImage_url(), options);
        return options.outHeight;
    }
}
