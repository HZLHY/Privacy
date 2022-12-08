package com.example.privacy;

import androidx.annotation.RequiresApi;
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

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class messEncActivity extends AppCompatActivity {
    private ArrayList<messageBean> messageEncBeans; // 加密的数据
    private MyDatabaseHelper databaseHelper;
    private AES myAes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mess_enc);
        databaseHelper = new MyDatabaseHelper(this);
        messageEncBeans = databaseHelper.queryAllEncMess();

        try {
            myAes = new AES(new BigInteger("120"));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        RecyclerView recyclerView = findViewById(R.id.messEncRecycle);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        messRecyAdapter myAdapter = new messRecyAdapter(messageEncBeans, this);
        myAdapter.setOnItemLongClickListener(new messRecyAdapter.OnItemLongClickListener() {
            @Override
            public void onItemLongClick(View view, int position) {
                int classId = messageEncBeans.get(position).getMessId();
                int queryId = messageEncBeans.get(position).getQueryId();
                String messEncBody = messageEncBeans.get(position).getBody();
                String number = messageEncBeans.get(position).getNumber();
                String name = messageEncBeans.get(position).getName();
                String pre=messageEncBeans.get(position).getPre();
                PopupMenu popupMenu = new PopupMenu(getApplicationContext(), view);
                popupMenu.getMenuInflater().inflate(R.menu.mess_decrypt_menu, popupMenu.getMenu());

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        if (menuItem.getItemId() == R.id.messDecryMenuItem) { // 对短信进行解密
                            messageEncBeans.remove(position);
                            myAdapter.notifyItemRemoved(position);
                            // 这里解密函数,得到解密的body
                            databaseHelper.deleteFromDbByEncBody(messEncBody); // 从表中删除
                            Toast.makeText(getApplicationContext(), "解密信息", Toast.LENGTH_SHORT).show();
//                            databaseHelper.insertMess(new messageBean(number, name, pre, queryId, classId));  // 加入加密前的短信分类表中
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