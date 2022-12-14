package com.example.privacy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bigkoo.svprogresshud.SVProgressHUD;
import com.bigkoo.svprogresshud.listener.OnDismissListener;
import com.example.privacy.Pyfunc.py;
import com.example.privacy.database.MyDatabaseHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import com.example.privacy.utils.createPicTxt;
import com.example.privacy.utils.datarequest;

public class ScanActivityPic extends AppCompatActivity {
    private ArrayList<PhotoItem> all_photo_set = new ArrayList<>();
    private ArrayList<PhotoItem> nowDataBasePhotoSet;
    private ArrayList<PhotoItem> tempDataBasePhotoSet;
    private MyDatabaseHelper myDatabaseHelper;
    private SVProgressHUD mSVProgressHUD;
    private SVProgressHUD SvUpLoadIngHUD;
    private ArrayList<String> paths;

    private final int SCAN_CODE_ING = 0x1000;
    private final int SCAN_CODE_END = 0x1001;
    private final String IP = "10.0.2.2";
    private final int PORT = 10001;
    private final String CLASSIFICATION_TXT_RES = "classificationTextRes.txt";
    private final String CLASSIFICATION_TXT = "classificationText.txt";
    int progress = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_pic);
        myDatabaseHelper = new MyDatabaseHelper(this);  //?????????MainActivity?????????????????????????????????
        // ???????????????????????????????????????
        nowDataBasePhotoSet = myDatabaseHelper.queryAllPhotoSet();
        verifyStoragePermissions(this); //????????????

        initEvent();  // ??????????????????
        // ?????????????????????????????????????????????
        mSVProgressHUD = new SVProgressHUD(this);
        SvUpLoadIngHUD = new SVProgressHUD(this);
        mSVProgressHUD.setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(SVProgressHUD hud) {
                Toast.makeText(getApplicationContext(), "????????????", Toast.LENGTH_SHORT).show();
            }
        });
        SvUpLoadIngHUD.setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(SVProgressHUD hud) {
                Toast.makeText(getApplicationContext(), "????????????", Toast.LENGTH_SHORT).show();
            }
        });

        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("parceUrl", all_photo_set);
        intent.putExtras(bundle);
        setResult(1, intent);

    }

    // ?????????????????????????????????????????????????????????
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == SCAN_CODE_ING) {
                mSVProgressHUD.getProgressBar().setProgress(msg.arg1);
                mSVProgressHUD.setText("?????? " + msg.arg1 + "%");
            } else if (msg.what == SCAN_CODE_END) {
                mSVProgressHUD.dismiss();
            }
        }
    };

    // ???????????????
    public void showWithProgress(View view) {
        progress = 0;
        mSVProgressHUD.getProgressBar().setProgress(progress);//?????????????????????????????????????????????show????????????????????????????????????????????????????????????0
        mSVProgressHUD.showWithProgress("?????? " + progress + "%", SVProgressHUD.SVProgressHUDMaskType.Black);
        mHandler.sendEmptyMessageDelayed(0, 500);
    }

    // ????????????
    public void showUpLoadIng(View view) {
        SvUpLoadIngHUD.showWithStatus("?????????...");
    }

    public void initEvent() {
        Button startButton = findViewById(R.id.startScanBtn);
        Button sendButton = findViewById(R.id.sendPicForClassBtn);
        Button tranButton2 = findViewById(R.id.sendPicForTran2);
        Button tranButton3 = findViewById(R.id.sendPicForTran3);
        createPicTxt mCreatePicTxt = new createPicTxt();
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showWithProgress(view);
                new Thread((Runnable) () -> {
                    scan_all_pic();
                }).start();
            }
        });

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showUpLoadIng(view);
                tempDataBasePhotoSet = all_photo_set.size()==0?nowDataBasePhotoSet:all_photo_set;
                new Thread((Runnable) () -> {
                mCreatePicTxt.createClassificationTxt(tempDataBasePhotoSet, "1", ScanActivityPic.this);
                datarequest dataSend = null;
                int Setsize = tempDataBasePhotoSet.size()+1;
                try {
                    dataSend = new datarequest(IP, PORT);
                    FileInputStream[] fileInputStreams = new FileInputStream[Setsize];
                    for (int i = 0; i < Setsize - 1; i++) {
                        fileInputStreams[i] = (new FileInputStream(tempDataBasePhotoSet.get(i).getImage_url()));
                    }
                    //????????????txt??????
                    fileInputStreams[Setsize-1] = new FileInputStream(getFilesDir() + "/" + CLASSIFICATION_TXT);

                    String resultStr = dataSend.getPhotosInfo(fileInputStreams);
                    py.print(resultStr);
                    dataSend.close();
                    setAllPhotoByString(tempDataBasePhotoSet, resultStr, myDatabaseHelper);// ????????????????????????????????????
                    SvUpLoadIngHUD.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                }).start();
            }
        });
        tranButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showUpLoadIng(view);
                new Thread((Runnable) () -> {
                    mCreatePicTxt.createClassificationTxt(nowDataBasePhotoSet, "2", ScanActivityPic.this);
                datarequest dataSend = null;
                int Setsize = nowDataBasePhotoSet.size() + 1;
                try {
                    dataSend = new datarequest(IP, PORT);
                    FileInputStream[] fileInputStreams = new FileInputStream[Setsize];
                    for (int i = 0; i < Setsize - 1; i++) {
                        py.print(nowDataBasePhotoSet.get(i).getImage_url());
                        fileInputStreams[i] = (new FileInputStream(nowDataBasePhotoSet.get(i).getImage_url()));
                    }
                    //????????????txt??????
                    fileInputStreams[Setsize-1] = new FileInputStream(getFilesDir() + "/" + CLASSIFICATION_TXT);

                    py.print(dataSend.getPhotosInfo(fileInputStreams));
                    SvUpLoadIngHUD.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                }).start();
            }
        });
        tranButton3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showUpLoadIng(view);
                new Thread((Runnable) () -> {
                    mCreatePicTxt.createClassificationTxt(nowDataBasePhotoSet, "3", ScanActivityPic.this);
                    datarequest dataSend = null;
                    int Setsize = nowDataBasePhotoSet.size() + 1;
                    try {
                        dataSend = new datarequest(IP, PORT);
                        FileInputStream[] fileInputStreams = new FileInputStream[Setsize];
                        for (int i = 0; i < Setsize - 1; i++) {
                            fileInputStreams[i] = (new FileInputStream(nowDataBasePhotoSet.get(i).getImage_url()));
                        }
                        //????????????txt??????
                        fileInputStreams[Setsize - 1] = new FileInputStream(getFilesDir() + "/" + "classificationText.txt");

                        py.print(dataSend.getPhotosInfo(fileInputStreams));
                        SvUpLoadIngHUD.dismiss();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }).start();
            }
        });
    }

    public static void verifyStoragePermissions(AppCompatActivity activity) {
        Toast.makeText(activity.getApplicationContext(), "????????????", Toast.LENGTH_SHORT).show();
        try {
            int permission = ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE
            );
            if (permission != PackageManager.PERMISSION_GRANTED) {
                // ???????????????????????????????????????
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            } else {
                Toast.makeText(activity.getApplicationContext(), "?????????", Toast.LENGTH_SHORT).show();
            }

            int permission2 = ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE
            );
            if (permission2 != PackageManager.PERMISSION_GRANTED) {
                // ???????????????????????????????????????
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 2);
            } else {
                Toast.makeText(activity.getApplicationContext(), "?????????", Toast.LENGTH_SHORT).show();
            }

            int permission3 = ContextCompat.checkSelfPermission(activity, Manifest.permission.INTERNET
            );
            if (permission3 != PackageManager.PERMISSION_GRANTED) {
                // ??????????????????????????????????????????
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.INTERNET}, 3);
            } else {
                Toast.makeText(activity.getApplicationContext(), "????????????", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void scan_all_pic() {
        paths = new ArrayList<>();
        ArrayList<String> names = new ArrayList<>();
        int totalPhotoNum = 0;
        int alreadyScan = 0;
        Cursor cursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, null, null, null);
        if (cursor != null) {
            totalPhotoNum = cursor.getCount();
            py.print(String.valueOf(totalPhotoNum));
        }
        py.print(String.valueOf(cursor.moveToNext()));
        while (cursor.moveToNext()) {
            if (cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME) > 0) {
                String name = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME));
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                String path = cursor.getString(column_index);
                names.add(name);
                paths.add(path);
                // recyclerview??????
                int albumId = classificationPicTest();
                PhotoItem photoItem = new PhotoItem(path, name, albumId);
                Log.i("GetImagesPath", "GetImagesPath:name = " + name + "    path = " + path + "  " + albumId);
                all_photo_set.add(photoItem);

                // ???????????????
                myDatabaseHelper.insertData(photoItem);

                alreadyScan = alreadyScan + 1;  //
                Message message = Message.obtain();
                message.arg1 = alreadyScan * 100 / totalPhotoNum;  // ???????????????
                message.what = SCAN_CODE_ING;
                mHandler.sendMessage(message);
            }
//            Message message = Message.obtain();
//            message.what = SCAN_CODE_END;
//            mHandler.sendMessage(message);
        }
        Message message = Message.obtain();
        message.what = SCAN_CODE_END;
        mHandler.sendMessage(message);
//        Log.i("GetImagesPath", paths.get(0));
        // ??????????????????????????????????????????????????????????????????????????????????????????????????????Android10????????????????????????????????????????????????????????????????????????????????????????????????????????????
        // ?????????????????????????????????????????????manifest ??? <application> ????????? android:requestLegacyExternalStorage = "true"
        cursor.close();
//        classificationPic(paths);
    }

    // ??????????????????????????????
    public int classificationPicTest() { // ?????????????????????????????????????????????????????????????????????????????????????????????url
        Random random = new Random();
        return random.nextInt(3);
    }

    // ???txt???????????????????????????????????????
    public void setAllPhotoSetByTxt(int photoNum) throws IOException {
        InputStream inputstream = new FileInputStream(getFilesDir() + "/classificationText.txt");
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputstream)); // ???????????????
        for (int i = 0; i < photoNum; i++) {
            String url = bufferedReader.readLine();
            int id = Integer.parseInt(bufferedReader.readLine());

        }
        inputstream.close();
        bufferedReader.close();
    }

    public void setAllPhotoByString(ArrayList<PhotoItem> nowDBPhotoSet, String resultStr, MyDatabaseHelper DbHelper) {
        String[] resSpstr = resultStr.split("\n");
        List<String> StrList = Arrays.asList(resSpstr);
        for (int i = 0; i < nowDBPhotoSet.size(); i++) {
            // ???????????????????????????
            DbHelper.updateDate(nowDBPhotoSet.get(i).getImage_url(), Integer.parseInt(StrList.get(i)));
        }
    }
}