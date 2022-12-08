package com.example.privacy;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.example.privacy.Crypto.ImageProc;
import com.example.privacy.adapter.encryptRecycleAdapter;
import com.example.privacy.bean.encryptDbBean;
import com.example.privacy.database.MyDatabaseHelper;

import java.math.BigInteger;
import java.util.ArrayList;

public class encryptActivity extends AppCompatActivity {
    private ArrayList<encryptDbBean> encryptDataList;
    private MyDatabaseHelper encryptDataBase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.encrypt_layout);
        encryptDataBase = new MyDatabaseHelper(this);

        // 查询加密表单
        encryptDataList = encryptDataBase.queryAllEncryptDataList();
        if(encryptDataList.size()==0){
            Toast.makeText(this, "没有加密数据", Toast.LENGTH_SHORT).show();
        }else {
            Log.i("[+]encryptSize", "size:" + encryptDataList.size());
        }



        RecyclerView recyclerView = findViewById(R.id.encryptRecycle);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(linearLayoutManager);
        encryptRecycleAdapter myAdapter = new encryptRecycleAdapter(encryptDataList,this);

        myAdapter.setOnItemLongClickListener(new encryptRecycleAdapter.OnItemLongClickListener() {
            @Override
            public void onItemLongClick(View view, int position) {
                String imageUrl = encryptDataList.get(position).getImageUrl();
                int classId =encryptDataList.get(position).getClassId();
                String encryptUrl = encryptDataList.get(position).getEncryptUrl();

                PopupMenu popupMenu =new PopupMenu(getApplicationContext(),view);
                popupMenu.getMenuInflater().inflate(R.menu.encrypt_menu,popupMenu.getMenu());

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        if(menuItem.getItemId()==R.id.encryptMenuItem){
                            encryptDataList.remove(position);
                            myAdapter.notifyItemRemoved(position);
                            decryptPic(imageUrl,encryptUrl,classId);
                            encryptDataBase.deleteFromTableThree(imageUrl); // 从表中删除
                            encryptDataBase.insertData(new PhotoItem(imageUrl,"name",classId));  // 加入加密前的相册分类表中
                        }
                        return false;
                    }
                });
                popupMenu.show();
            }
        });
        recyclerView.setAdapter(myAdapter);
    }
    public void decryptPic(String originUrl,String encryptUrl,int albumId){
        Toast.makeText(getApplicationContext(), "解密成功！", Toast.LENGTH_SHORT).show();
    }
}