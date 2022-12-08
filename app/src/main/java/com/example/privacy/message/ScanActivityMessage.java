package com.example.privacy.message;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.telephony.SmsMessage;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bigkoo.svprogresshud.SVProgressHUD;
import com.bigkoo.svprogresshud.listener.OnDismissListener;
import com.example.privacy.Pyfunc.py;
import com.example.privacy.R;
import com.example.privacy.bean.messageBean;
import com.example.privacy.database.MyDatabaseHelper;
import com.example.privacy.utils.createMessTxt;
import com.example.privacy.utils.datarequest;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class ScanActivityMessage extends AppCompatActivity {
    private Uri SMS_INBOX = Uri.parse("content://sms/");
    private MyDatabaseHelper databaseHelper;
    private ArrayList<messageBean> messageBeans;  // 本次扫描到的
    private ArrayList<messageBean> nowDataBaseMess;
    private ArrayList<messageBean> tempDataBaseMess;
    private final String MESS_CLASS_TXT = "messClassText.txt"; // 用于短信的分类
    private final String MESS_TEMP_BODY = "messBody";
    private final String IP = "10.0.2.2";
    private final int PORT = 10001;

    private SVProgressHUD mSVProgressHUD;
    private SVProgressHUD SvUpLoadIngHUD;

    private final int SCAN_CODE_ING = 0x1000;
    private final int SCAN_CODE_END = 0x1001;
    int progress = 0;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_mess);
        databaseHelper = new MyDatabaseHelper(this);
        verifyStoragePermissions(this);
        nowDataBaseMess = databaseHelper.queryAllMess(); // 获取目前数据库中的数据
        InitEvent();

        mSVProgressHUD = new SVProgressHUD(this);
        SvUpLoadIngHUD = new SVProgressHUD(this);
        mSVProgressHUD.setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(SVProgressHUD hud) {
                Toast.makeText(getApplicationContext(), "扫描成功", Toast.LENGTH_SHORT).show();
            }
        });
        SvUpLoadIngHUD.setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(SVProgressHUD hud) {
                Toast.makeText(getApplicationContext(), "发送成功", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == SCAN_CODE_ING) {
                mSVProgressHUD.getProgressBar().setProgress(msg.arg1);
                mSVProgressHUD.setText("进度 " + msg.arg1 + "%");
            } else if (msg.what == SCAN_CODE_END) {
                mSVProgressHUD.dismiss();
            }
        }
    };
    // 扫描进度条
    public void showWithProgress(View view) {
        progress = 0;
        mSVProgressHUD.getProgressBar().setProgress(progress);//先重设了进度再显示，避免下次再show会先显示上一次的进度位置所以要先将进度归0
        mSVProgressHUD.showWithProgress("进度 " + progress + "%", SVProgressHUD.SVProgressHUDMaskType.Black);
        mHandler.sendEmptyMessageDelayed(0, 500);
    }
    // 上传动画
    public void showUpLoadIng(View view) {
        SvUpLoadIngHUD.showWithStatus("发送中...");
    }

    public static void verifyStoragePermissions(AppCompatActivity activity) {
        Toast.makeText(activity.getApplicationContext(), "扫描短信", Toast.LENGTH_SHORT).show();
        try {
            int permission = ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_SMS
            );
            if (permission != PackageManager.PERMISSION_GRANTED) {
                // 若没有读短信的权限，则申请权限
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_SMS}, 1);
            } else {
                Toast.makeText(activity.getApplicationContext(), "短信权限", Toast.LENGTH_SHORT).show();
            }

            int permission2 = ContextCompat.checkSelfPermission(activity, Manifest.permission.RECEIVE_SMS
            );
            if (permission2 != PackageManager.PERMISSION_GRANTED) {
                // 若没有读的权限，则申请权限
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.RECEIVE_SMS}, 1);
            } else {
                Toast.makeText(activity.getApplicationContext(), "收取短信权限", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void scan_all_mess() {// 扫描短信
        int totalMessNum =0;
        int alreadyScan=0;
        Random random = new Random();
        messageBeans = new ArrayList<>();
        String[] projection = new String[]{"_id", "address", "person", "body", "date", "type"};
        Cursor cursor = getContentResolver().query(SMS_INBOX, projection, null, null, "date desc");
        if (cursor != null) {
            totalMessNum = cursor.getCount();
        }
        int i = 0;
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String number = cursor.getString(cursor.getColumnIndexOrThrow("address")); // 手机号
//                String name = cursor.getString(cursor.getColumnIndexOrThrow("person")); //联系人
                String body = cursor.getString(cursor.getColumnIndexOrThrow("body")); // 短信内容
                Log.i("message", number + "  " + " " + "  " + body);

                // 将扫描到的插入数据库
                messageBean messBean = new messageBean(number, " ", body, i, random.nextInt(2));
                databaseHelper.insertMess(messBean);
                messageBeans.add(messBean);
                i++;
                alreadyScan = alreadyScan + 1;  //
                Message message = Message.obtain();
                message.arg1 = alreadyScan * 100 / totalMessNum;  // 进度百分比
                message.what = SCAN_CODE_ING;
                mHandler.sendMessage(message);
            }
            Message message = Message.obtain();
            message.what = SCAN_CODE_END;
            mHandler.sendMessage(message);
            cursor.close();
        }
    }

    public void InitEvent() {
        Button startButton = findViewById(R.id.messScanBtn);
        Button sendButton = findViewById(R.id.sendMessForClassBtn);
        Button tranButton2 = findViewById(R.id.sendMessForTran2);
        Button tranButton3 = findViewById(R.id.sendMessForTran3);
        createMessTxt mCreateMessReTxt = new createMessTxt();
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showWithProgress(view);
                new Thread((Runnable)()->{
                    scan_all_mess();
                } ).start();
            }
        });
        sendButton.setOnClickListener(new View.OnClickListener() {  // 请求分类
            @Override
            public void onClick(View view) {
                showUpLoadIng(view);
                tempDataBaseMess= messageBeans.size()==0?nowDataBaseMess:messageBeans;
                new Thread((Runnable) () -> {
                    mCreateMessReTxt.createMessClassRequest(tempDataBaseMess, "1", ScanActivityMessage.this);
                    datarequest dataSend = null;
                    int setSize = tempDataBaseMess.size() + 1;
                    try {
                        dataSend = new datarequest(IP, PORT);
                        String[] sendString = new String[setSize];
                        for (int i = 0; i < setSize - 1; i++) {
                            sendString[i] = tempDataBaseMess.get(i).getBody();
                        }
                        FileInputStream fileInputStream = openFileInput(MESS_CLASS_TXT);
                        int len = 0;
                        byte[] buf = new byte[1024];
                        StringBuilder sb = new StringBuilder();
                        while ((len = fileInputStream.read(buf)) != -1) {
                            sb.append(new String(buf, 0, len));
                        }
                        fileInputStream.close();
                        sendString[setSize - 1] = sb.toString();

                        String resultStr = dataSend.getTextInfo(sendString);
//                        String tempString = "0\n0\n0\n0\n1\n1\n1";
                        setAllMessByString(tempDataBaseMess,resultStr,databaseHelper);
                        SvUpLoadIngHUD.dismiss();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }).start();
            }
        });

        tranButton2.setOnClickListener(new View.OnClickListener() {  // 从头训练
            @Override
            public void onClick(View view) {
                showUpLoadIng(view);
                new Thread((Runnable) () -> {
                    mCreateMessReTxt.createMessClassRequest(nowDataBaseMess, "2", ScanActivityMessage.this);
                    datarequest dataSend = null;
                    int setSize = nowDataBaseMess.size() + 1;
                    try {
                        dataSend = new datarequest(IP, PORT);
                        String[] sendString = new String[setSize];
                        for (int i = 0; i < setSize - 1; i++) {
                            sendString[i] = nowDataBaseMess.get(i).getBody();
                        }
                        FileInputStream fileInputStream = openFileInput(MESS_CLASS_TXT);
                        int len = 0;
                        byte[] buf = new byte[1024];
                        StringBuilder sb = new StringBuilder();
                        while ((len = fileInputStream.read(buf)) != -1) {
                            sb.append(new String(buf, 0, len));
                        }
                        fileInputStream.close();
                        sendString[setSize - 1] = sb.toString();

                        py.print(dataSend.getTextInfo(sendString));
                        showUpLoadIng(view);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }).start();
            }
        });

        tranButton3.setOnClickListener(new View.OnClickListener() { //接着训练
            @Override
            public void onClick(View view) {
                showUpLoadIng(view);
                new Thread((Runnable) () -> {
                    mCreateMessReTxt.createMessClassRequest(nowDataBaseMess, "3", ScanActivityMessage.this);
                    datarequest dataSend = null;
                    int setSize = nowDataBaseMess.size() + 1;
                    try {
                        dataSend = new datarequest(IP, PORT);
                        String[] sendString = new String[setSize];
                        for (int i = 0; i < setSize - 1; i++) {
                            sendString[i] = nowDataBaseMess.get(i).getBody();
                        }
                        FileInputStream fileInputStream = openFileInput(MESS_CLASS_TXT);
                        int len = 0;
                        byte[] buf = new byte[1024];
                        StringBuilder sb = new StringBuilder();
                        while ((len = fileInputStream.read(buf)) != -1) {
                            sb.append(new String(buf, 0, len));
                        }
                        fileInputStream.close();
                        sendString[setSize - 1] = sb.toString();

                        py.print(dataSend.getTextInfo(sendString));
                        showUpLoadIng(view);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }).start();
            }
        });
    }
    // 创建请求按option看是否分类

    public void setAllMessByString(ArrayList<messageBean> messageBeans, String resultStr, MyDatabaseHelper DbHelper) {
        String[] resSpStr = resultStr.split("\n");
        List<String> StrList = Arrays.asList(resSpStr);
        for (int i = 0; i < messageBeans.size(); i++) {
            DbHelper.updateMessClass(messageBeans.get(i), Integer.parseInt(StrList.get(i)));
        }
    }
}
