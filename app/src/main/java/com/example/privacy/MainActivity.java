package com.example.privacy;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.privacy.Pyfunc.py;
import com.example.privacy.database.MyDatabaseHelper;
import com.example.privacy.message.ScanActivityMessage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {  // 需要分别写点击监听，否则，点了底部后，listview不能点击
    ViewPager viewPagerOne;
    private LinearLayout bottom_layout1, bottom_layout2, bottom_layout3;
    private ImageView bottom_pic1, bottom_pic2, bottom_pic3, bottomPciCur;
    private ArrayList<Fragment> fragments = new ArrayList<>();
    private ArrayList<PhotoItem> photoItems = new ArrayList<>();
    private ArrayList<PhotoItem> photoItemsOne = new ArrayList<>();
    private ArrayList<PhotoItem> photoItemsTwo = new ArrayList<>();
    private ArrayList<PhotoItem> photoItemsThree = new ArrayList<>();
    private MyDatabaseHelper dbHelper;
    private ArrayList<PhotoItem> sendPhotoSet;
    private final String CLASSIFICATION_TXT = "classificationText.txt";
    private final String SERVER_URL = "https://www.httpbin.org/post";  // 上传服务器的名称

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // 创建数据库
        dbHelper = new MyDatabaseHelper(this);
        // 测试
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query("albumOne", null, null, null, null, null, null);
        if (cursor != null) {
            sendPhotoSet = new ArrayList<>();
            while (cursor.moveToNext()) {
                photoItems.add(new PhotoItem(cursor.getString(1), "test", cursor.getInt(2)));
                sendPhotoSet.add(new PhotoItem(cursor.getString(1), "sendName", cursor.getInt(2)));
                Log.i("DatabaseMainActivity", photoItems.get(photoItems.size() - 1).getImage_url());
            }
            cursor.close();
        }
        initTabView();
        initPager();
        initToolbar();
    }

    // 接受扫描返回的数据
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 2:
                if (resultCode == 1) {
                    ArrayList<PhotoItem> sendPhotosRes = dbHelper.queryAllPhotoSet();
                    Toast.makeText(this, "扫描返回", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.myToolbar);
        // 设置overflow菜单图标，大小如何解决？？？
//        toolbar.setOverflowIcon(getResources().getDrawable(R.drawable.plus));
        // 填充menu
        toolbar.inflateMenu(R.menu.toolbar_menu);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.scan_message:
                        Toast.makeText(MainActivity.this, "扫描短信", Toast.LENGTH_SHORT).show();
                        Intent intentMess = new Intent(MainActivity.this, ScanActivityMessage.class);
                        startActivity(intentMess);
                        break;
                    case R.id.scan_pic:
                        Toast.makeText(MainActivity.this, "扫描图片", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(MainActivity.this, ScanActivityPic.class);
                        startActivityForResult(intent, 2);
                        break;
                    case R.id.scan_music:
                        Toast.makeText(MainActivity.this, "扫描音频", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.settings:
                        Toast.makeText(MainActivity.this, "进入设置", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        break;
                }
                return false;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    private void initTabView() {
        bottom_layout1 = findViewById(R.id.bottom_layout1);
        bottom_layout1.setOnClickListener(bottomTap);
        bottom_layout2 = findViewById(R.id.bottom_layout2);
        bottom_layout2.setOnClickListener(bottomTap);
        bottom_layout3 = findViewById(R.id.bottom_layout3);
        bottom_layout3.setOnClickListener(bottomTap);

        bottom_pic1 = findViewById(R.id.bottom_image1);
        bottom_pic2 = findViewById(R.id.bottom_image2);
        bottom_pic3 = findViewById(R.id.bottom_image3);

        bottom_pic1.setSelected(true);
        bottomPciCur = bottom_pic1;
    }

    private void initPager() {
        viewPagerOne = findViewById(R.id.id_viewpager);
        fragments.add(new homeFragViewpager1());
        fragments.add(new homeFragViewpager2());
        fragments.add(new homeFragViewpager3());
        homeFragmentAdapter myFragAdapter = new homeFragmentAdapter(fragments, getSupportFragmentManager());
        viewPagerOne.setAdapter(myFragAdapter);
        viewPagerOne.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        changTab(0);
                        break;
                    case 1:
                        changTab(1);
                        break;
                    case 2:
                        changTab(2);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void changTab(int position) {
        bottomPciCur.setSelected(false);
        switch (position) {
            case 0:
                bottom_pic1.setSelected(true);
                bottomPciCur = bottom_pic1;
                break;
            case 1:
                bottom_pic2.setSelected(true);
                bottomPciCur = bottom_pic2;
                break;
            case 2:
                bottom_pic3.setSelected(true);
                bottomPciCur = bottom_pic3;
                break;
        }
    }

    private View.OnClickListener bottomTap = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            if (view.getId() == R.id.bottom_layout1) {
                viewPagerOne.setCurrentItem(0);
                changTab(0);
            } else if (view.getId() == R.id.bottom_layout2) {
                viewPagerOne.setCurrentItem(1);
                changTab(1);
            } else if (view.getId() == R.id.bottom_layout3) {
                viewPagerOne.setCurrentItem(2);
                changTab(2);
            }
        }
    };

    public void sendClassification() { // 发送手动分类结果，数据是写入txt中的
        File file = new File(getFilesDir() + "/" + CLASSIFICATION_TXT);
        OkHttpClient client = new OkHttpClient();
        // 参数应该怎么传递？
        if (file.exists()) {
            MultipartBody.Builder requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM);
            RequestBody body = RequestBody.create(MediaType.parse("text/plain"), file);
            requestBody.addFormDataPart("classTxtFile", CLASSIFICATION_TXT, body);
            requestBody.addFormDataPart("classPicRequest", "2");
            Request request = new Request.Builder()
                    .url(SERVER_URL)
                    .post(requestBody.build())
                    .build();
            Call call = client.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.i("UpLoadTxt", "文件上传onFailure: " + e);
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    Log.i("UpLoadTxt", "文件上传onResponse: " + response.body().string());
                }
            });
        } else {
            Log.i("UpLoadTxt", "file not exists " + getFilesDir());
        }
    }
    // 创建手动分类txt，约定第一行的字符为2，表示发送手动分类文件让服务器进行训练，按顺序写入分类id
//    public void createClassificationTxt(ArrayList<PhotoItem> sendPhotoSet) {
//        String enterLine = System.getProperty("line.separator");
//        try {
//            FileOutputStream outputStream = openFileOutput(CLASSIFICATION_TXT, Context.MODE_PRIVATE);
//            outputStream.write("2".getBytes());  //表示发送手动分类让服务器进行训练
//            outputStream.write(enterLine.getBytes());
//            for (int i=0;i<sendPhotoSet.size();i++){
//                outputStream.write(String.valueOf(sendPhotoSet.get(i).getAlbumId()).getBytes());
//                outputStream.write(enterLine.getBytes());
//            }
//            outputStream.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

//    public void setAllPhotoSetByTxt(int photoNum) throws IOException {
//        InputStream inputstream = new FileInputStream(getFilesDir() + "/classificationText.txt");
//        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputstream)); // 创建字符流
//        for (int i = 0; i < photoNum+1; i++) {
//            String id = bufferedReader.readLine();
//            py.print(String.valueOf(id));
//        }
//        inputstream.close();
//        bufferedReader.close();
//    }
}