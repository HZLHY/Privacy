package com.example.privacy;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.example.privacy.Crypto.Util.AES;
import com.example.privacy.Pyfunc.py;
import com.example.privacy.R;
import com.example.privacy.adapter.messRecyAdapter;
import com.example.privacy.bean.messageBean;
import com.example.privacy.database.MyDatabaseHelper;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
//import android.util.Base64;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class messRecyActivity extends AppCompatActivity {

    private ArrayList<messageBean> messList;
    private MyDatabaseHelper messageDataBase;
    private int classId;
    private AES myAes;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mess_recy);
        messageDataBase = new MyDatabaseHelper(this);
        Bundle bundle = getIntent().getExtras();
        classId = bundle.getInt("messageSetId");
        // 从bundle中获取该相册的classIa
        messList = messageDataBase.queryMessByClassId(classId);

        try {
             myAes = new AES(new BigInteger("120"));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        RecyclerView recyclerView = findViewById(R.id.messReInner); // 获取布局中的recyclerview

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);

        messRecyAdapter myAdapter = new messRecyAdapter(messList, this);

        myAdapter.setOnItemLongClickListener(new messRecyAdapter.OnItemLongClickListener() {
            @Override
            public void onItemLongClick(View view, int position) {
                int queryId = messList.get(position).getQueryId();
                int classId = messList.get(position).getMessId();
                String body = messList.get(position).getBody();
                String name = messList.get(position).getName();
                String number = messList.get(position).getNumber();

                PopupMenu popupMenu = new PopupMenu(getApplicationContext(), view);
                popupMenu.getMenuInflater().inflate(R.menu.mess_item_menu, popupMenu.getMenu());

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        if (menuItem.getItemId() == R.id.messMenuItem) {
                            // 按body内容删除
                            messageDataBase.deleteFromDbByBody(messList.get(position).getBody());
                            // 下面是加密函数
                            try {
                                // 进行AES+base64编码,
                                String EncBody =  myAes.Encrypt(body);
                                messList.get(position).setBody(EncBody);
                                messageBean messageBeanEnc=new messageBean(number,name,EncBody,queryId,classId);
                                messageBeanEnc.setPre(body);
                                messageDataBase.insertMessEnc(messList.get(position),body);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            messList.remove(position);
                            myAdapter.notifyItemRemoved(position);
                            Toast.makeText(getApplicationContext(), "加密短信成功", Toast.LENGTH_SHORT).show();
                        }
                        return false;
                    }
                });
                popupMenu.show();
            }
        });
        recyclerView.setAdapter(myAdapter);
    }
}