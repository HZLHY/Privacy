package com.example.privacy;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.privacy.database.MyDatabaseHelper;

import java.util.ArrayList;

public class addAlbumActivity extends AppCompatActivity {
    private EditText editText;
    private MyDatabaseHelper albumDataBase;
    private ArrayList<album> albums;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_album);
        editText = findViewById(R.id.et_title);
        albumDataBase = new MyDatabaseHelper(this);
        albums = albumDataBase.queryAllAlbum();
    }
    public void add(View view){
        String title = editText.getText().toString();
        album newAlbum = new album(null,title,R.drawable.community,albums.size());
        long row = albumDataBase.insertAlbum(newAlbum);
        if(row!=-1){
            Toast.makeText(this, "添加成功！", Toast.LENGTH_SHORT).show();
            this.finish();
        }else {
            Toast.makeText(this, "添加失败！", Toast.LENGTH_SHORT).show();
        }
    }
}