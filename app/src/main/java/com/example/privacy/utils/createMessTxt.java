package com.example.privacy.utils;

import android.content.Context;

import com.example.privacy.bean.messageBean;

import java.io.FileOutputStream;
import java.util.ArrayList;

public class createMessTxt {  // 创建请求信息
    private final String MESS_CLASS_TXT = "messClassText.txt"; // 用于短信的分类
    public void createMessClassRequest(ArrayList<messageBean> messageBeans, String option, Context mContext){
        String enterLine = System.getProperty("line.separator");
        try {
            FileOutputStream outputStream = mContext.openFileOutput(MESS_CLASS_TXT,Context.MODE_PRIVATE);
            if(option.equals("1")){
                outputStream.write(option.getBytes());  //表示请求进行分类的txt
                outputStream.write(enterLine.getBytes());
            }else {
                outputStream.write(option.getBytes());  //表示发送手动分类让服务器进行训练
                outputStream.write(enterLine.getBytes());
                for (int i=0;i<messageBeans.size();i++){
                    outputStream.write(String.valueOf(messageBeans.get(i).getMessId()).getBytes());
                    outputStream.write(enterLine.getBytes());
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
