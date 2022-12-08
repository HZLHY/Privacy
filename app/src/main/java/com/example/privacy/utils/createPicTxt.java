package com.example.privacy.utils;

import android.content.Context;

import com.example.privacy.PhotoItem;

import java.io.FileOutputStream;

import android.content.Context;

import java.util.ArrayList;

public class createPicTxt {
    private final String CLASSIFICATION_TXT = "classificationText.txt";

    public void createClassificationTxt(ArrayList<PhotoItem> sendPhotoSet, String option, Context mContext) {
        String enterLine = System.getProperty("line.separator");
        try {
            FileOutputStream outputStream = mContext.openFileOutput(CLASSIFICATION_TXT, Context.MODE_PRIVATE);
            if (option.equals("1")) {
                outputStream.write(option.getBytes());  //表示请求进行分类的txt
                outputStream.write(enterLine.getBytes());
            } else {
                outputStream.write(option.getBytes());  //表示发送手动分类让服务器进行训练
                outputStream.write(enterLine.getBytes());
                for (int i = 0; i < sendPhotoSet.size(); i++) {
                    outputStream.write(String.valueOf(sendPhotoSet.get(i).getAlbumId()).getBytes());
                    outputStream.write(enterLine.getBytes());
                }
            }
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
