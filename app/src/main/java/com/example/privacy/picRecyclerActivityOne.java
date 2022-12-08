package com.example.privacy;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.database.sqlite.SQLiteDatabase;
import android.icu.util.BuddhistCalendar;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.example.privacy.Crypto.ImageProc;
import com.example.privacy.database.MyDatabaseHelper;
import com.example.privacy.utils.getFileNameFromPath;

import java.math.BigInteger;
import java.util.ArrayList;

public class picRecyclerActivityOne extends AppCompatActivity {
    private MyDatabaseHelper dbHelper;
    private ArrayList<album> albumArrayList;
    private String encryptDir;
    private ImageProc ImageEncProc;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pic_recycler_one);
        dbHelper = new MyDatabaseHelper(this);
        // 测试recyclerview
//        Bundle bundle = getIntent().getExtras();
//        ArrayList<PhotoItem> photoItems = bundle.getParcelableArrayList("parceUrlRecycle");
        Bundle bundle = getIntent().getExtras();
        int queryId = bundle.getInt("albumId");
//        albumNum = bundle.getInt("albumNum");

        // 从数据库查询照片路径
        ArrayList<PhotoItem> photoItems = dbHelper.queryFromDbById(queryId);
        albumArrayList = dbHelper.queryAllAlbum();

        Log.i("[+]album", "size:" + albumArrayList.size());
        // 从数据库查询相册相关信息

        //设置密钥
        ImageEncProc = new ImageProc(new BigInteger("1203453"));

        encryptDir = getFilesDir().getPath();

        RecyclerView recyclerView = findViewById(R.id.picRecycler1);
        recyclerView.setPadding(8, 8, 8, 8);
        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, LinearLayout.VERTICAL);

        recyclerView.setLayoutManager(staggeredGridLayoutManager);
        RecycleAdapterOne myAdapter = new RecycleAdapterOne(photoItems, this);

        // 长按弹窗测试
        myAdapter.setOnItemClickListener(new RecycleAdapterOne.OnItemClickListener() {
            @Override
            public void onItemLongClick(View view, int position) {
                StringBuilder stringBuilder = new StringBuilder();
                String imageUrl = photoItems.get(position).getImage_url();
                PopupMenu popupMenu = new PopupMenu(getApplicationContext(), view);
                popupMenu.getMenuInflater().inflate(R.menu.recycleritem_menu, popupMenu.getMenu());
                for (int i = 0; i < albumArrayList.size(); i++) {
                    if (i != queryId) {
                        popupMenu.getMenu().add(Menu.NONE, Menu.FIRST + i, i, stringBuilder.append("移动到").append(albumArrayList.get(i).getAlbum_name()).toString());
                        stringBuilder.setLength(0);
                    }
                }

                // 弹出式菜单的菜单项点击事件,测试成功
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.removeItem:
                                photoItems.remove(position);
                                myAdapter.notifyItemRemoved(position);
                                // 删除成功
                                dbHelper.deleteFromDbByUrl(imageUrl);
                                break;
                            case R.id.encryptItem:
                                photoItems.remove(position);
                                myAdapter.notifyItemRemoved(position);
                                dbHelper.deleteFromDbByUrl(imageUrl);
                                getFileNameFromPath getNameEnc = new getFileNameFromPath();
//                                encryptDir = getFilesDir();
                                Log.i("Encrypt",encryptDir);
//                                ImageEncProc.Encrypt(encryptDir+"/classificationText.txt",encryptDir+"/"+getNameEnc.getName(imageUrl));
//                                ImageEncProc.Encrypt(imageUrl,encryptDir+"/"+getNameEnc.getName(imageUrl));
                                dbHelper.insertEncrypt(imageUrl,queryId,encryptDir);
                                break;
                            case Menu.FIRST + 1:
                                photoItems.remove(position);
                                myAdapter.notifyItemRemoved(position);
                                dbHelper.updateDate(imageUrl, 1);
                                Toast.makeText(getApplicationContext(), "移动图片到证件相册", Toast.LENGTH_SHORT).show();
                                break;
                            case Menu.FIRST + 0:
                                photoItems.remove(position);
                                myAdapter.notifyItemRemoved(position);
                                dbHelper.updateDate(imageUrl, 0);
                                Toast.makeText(getApplicationContext(), "移动图片到人物相册", Toast.LENGTH_SHORT).show();
                                break;
                            case Menu.FIRST + 2:
                                photoItems.remove(position);
                                myAdapter.notifyItemRemoved(position);
                                dbHelper.updateDate(imageUrl, 2);
                                Toast.makeText(getApplicationContext(), "移动图片到文件相册", Toast.LENGTH_SHORT).show();
                                break;
                            case Menu.FIRST + 3:
                                photoItems.remove(position);
                                myAdapter.notifyItemRemoved(position);
                                dbHelper.updateDate(imageUrl, 3);
                                Toast.makeText(getApplicationContext(), "移动图片到其它相册", Toast.LENGTH_SHORT).show();
                                break;
                            default:
                                for(int i=Menu.FIRST+4;i<albumArrayList.size()+1;i++){
                                    if(menuItem.getItemId()==i){
                                        photoItems.remove(position);
                                        myAdapter.notifyItemRemoved(position);
                                        dbHelper.updateDate(imageUrl, i-1);
                                        Toast.makeText(getApplicationContext(), "移动图片到"+albumArrayList.get(i-1).getAlbum_name(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                                break;
                        }
                        return false;
                    }
                });
                popupMenu.show();
            }
        });

        recyclerView.setAdapter(myAdapter);

    }
    public void encryptPic(String originUrl,String encryptUrl,int albumId){
        Toast.makeText(getApplicationContext(), "加密成功！", Toast.LENGTH_SHORT).show();
    }
}