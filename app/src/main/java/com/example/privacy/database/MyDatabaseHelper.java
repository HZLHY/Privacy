package com.example.privacy.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.privacy.PhotoItem;
import com.example.privacy.R;
import com.example.privacy.album;
import com.example.privacy.bean.encryptDbBean;
import com.example.privacy.bean.messageBean;

import java.util.ArrayList;
import java.util.HashMap;

public class MyDatabaseHelper extends SQLiteOpenHelper {
    private Context myContext;
    private static final String DB_NAME = "myAlbum.db";  // 数据库名字
    private static final String TABLE_ONE_NAME = "albumOne"; // 存放图片的相册分类信息
    private static final String TABLE_TWO_NAME = "albumList"; // 存放相册信息
    private static final String TABLE_THREE_NAME = "encryption"; // 存放加密前与加密后的路径与名字
    private static final String TABLE_MESSAGE = "messageDb"; // 存放未加密的信息
    private static final String TABLE_MESS_ENC = "messEncDb";
    private static final String TABLE_AUDIO = "audioDb"; // 存放未加密的音频
    private static final String TABLE_AUDIO_ENC = "audioEncDb";

    public MyDatabaseHelper(Context context) {
        super(context, DB_NAME, null, 1);
    }

    public MyDatabaseHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        myContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String sqlOne = "create table if not exists " + TABLE_ONE_NAME + " (Id integer primary key autoincrement, imageUrl text, classification integer)";
        String sqlTwo = "create table if not exists " + TABLE_TWO_NAME + " (Id integer primary key autoincrement, albumName text, classification integer)";
        String sqlThree = "create table if not exists " + TABLE_THREE_NAME + " (Id integer primary key autoincrement, imageUrl text, classification integer,encryptUrl text)";
        String sqlFour = "create table if not exists " + TABLE_MESSAGE + " (Id integer primary key autoincrement, number text, name text, body text,queryId integer,classification integer)";
        String sqlFive = "create table if not exists " + TABLE_MESS_ENC + " (Id integer primary key autoincrement, number text, name text,prebody text ,body text,queryId integer,classification integer)";
        sqLiteDatabase.execSQL(sqlOne);
        sqLiteDatabase.execSQL(sqlTwo);
        sqLiteDatabase.execSQL(sqlThree);
        sqLiteDatabase.execSQL(sqlFour);
        sqLiteDatabase.execSQL(sqlFive);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public void insertData(PhotoItem photoItem) {
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.query(TABLE_ONE_NAME, new String[]{"imageUrl"}, "imageUrl=?", new String[]{photoItem.getImage_url()}, null, null, null);
        if (cursor.getCount() == 0) {
            ContentValues values = new ContentValues();
            values.put("imageUrl", photoItem.getImage_url());
            values.put("classification", photoItem.getAlbumId());
            db.insert(TABLE_ONE_NAME, null, values);
        } else {
            Log.i("Insert:", "数据库中已经存在" + photoItem.getImage_url());
        }
        cursor.close();
    }

    public int deleteFromDbByUrl(String url) {
        SQLiteDatabase db = getWritableDatabase();
        // 返回值为删除的条数,按url删除
        return db.delete(TABLE_ONE_NAME, "imageUrl like ?", new String[]{url});
    }

    public int updateDate(String imageUrl, int targetAlbumId) {// 按url与目的id修改
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("imageUrl", imageUrl);
        values.put("classification", targetAlbumId);

        return db.update(TABLE_ONE_NAME, values, "imageUrl like ?", new String[]{imageUrl});
    }

    public ArrayList<PhotoItem> queryFromDbById(int targetAlbumId) {
        SQLiteDatabase db = getReadableDatabase();
        ArrayList<PhotoItem> photoItems = new ArrayList<>(); // 根据id查询语句应该如何写？
        Cursor cursor = db.query(TABLE_ONE_NAME, null, "classification =" + targetAlbumId + "", null, null, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                photoItems.add(new PhotoItem(cursor.getString(1), "", cursor.getInt(2)));

            }
            cursor.close();
        }
        Log.i("[databasequery]", "size:" + photoItems.size());
        return photoItems;
    }

    // 返回全部照片的路径以及分类id
    public ArrayList<PhotoItem> queryAllPhotoSet() {
        SQLiteDatabase db = getReadableDatabase();
        ArrayList<PhotoItem> allPhotoItems = new ArrayList<>();
        Cursor cursor = db.query(TABLE_ONE_NAME, null, null, null, null, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                allPhotoItems.add(new PhotoItem(cursor.getString(1), "testName", cursor.getInt(2)));
            }
            cursor.close();
        }
        return allPhotoItems;
    }

    // 相册列表的数据库操作
    public long insertAlbum(album albumItem) {
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.query(TABLE_TWO_NAME, new String[]{"albumName"}, "albumName=?", new String[]{albumItem.getAlbum_name()}, null, null, null);
        if (cursor.getCount() == 0) {
            ContentValues values = new ContentValues();
            values.put("albumName", albumItem.getAlbum_name());
            values.put("classification", albumItem.getAlbumId());
            cursor.close();
            return db.insert(TABLE_TWO_NAME, null, values);
        } else {
            cursor.close();
            return -1;
        }
    }

    public int deleteFromTableTwoByName(String name) {
        SQLiteDatabase db = getWritableDatabase();
        return db.delete(TABLE_TWO_NAME, "albumName like ?", new String[]{name});
    }

    public ArrayList<album> queryAllAlbum() {
        SQLiteDatabase db = getReadableDatabase();
        ArrayList<album> albums = new ArrayList<>();
        Cursor cursor = db.query(TABLE_TWO_NAME, null, null, null, null, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                albums.add(new album(null, cursor.getString(1), R.drawable.unknown, cursor.getInt(2)));
            }
            cursor.close();
        }
        return albums;
    }

    // 加密数据库的存储
    public void insertEncrypt(String imageUrl, int classId, String encryptUrl) {
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.query(TABLE_THREE_NAME, new String[]{"imageUrl"}, "imageUrl=?", new String[]{imageUrl}, null, null, null);
        if (cursor.getCount() == 0) {
            ContentValues values = new ContentValues();
            values.put("imageUrl", imageUrl);
            values.put("classification", classId);
            values.put("encryptUrl", encryptUrl);
            db.insert(TABLE_THREE_NAME, null, values);
        }
        cursor.close();
    }

    // 根据路径与名字删除
    public int deleteFromTableThree(String url) {
        SQLiteDatabase db = getWritableDatabase();
        return db.delete(TABLE_THREE_NAME, "imageUrl like ?", new String[]{url});
    }

    // 查询所有加密数据库文件
    public ArrayList<encryptDbBean> queryAllEncryptDataList() {
        ArrayList<encryptDbBean> encryptDataList = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_THREE_NAME, null, null, null, null, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                encryptDataList.add(new encryptDbBean(cursor.getString(1), cursor.getInt(2), cursor.getString(3)));
            }
            cursor.close();
        }
        return encryptDataList;
    }

    //短信表
    public long insertMess(messageBean messItem) {
        SQLiteDatabase db = getWritableDatabase();
        String body = messItem.getBody();
        Cursor cursor = db.query(TABLE_MESSAGE, new String[]{"body"}, "body = ?", new String[]{body}, null, null, null);
        if (cursor.getCount() == 0) {
            ContentValues values = new ContentValues();
            values.put("number", messItem.getNumber());
            values.put("name", messItem.getName());
            values.put("body", messItem.getBody());
            values.put("queryId", messItem.getQueryId());
            values.put("classification", messItem.getMessId());
            return db.insert(TABLE_MESSAGE, null, values);
        }
        cursor.close();
        return -1;
    }

    // 查询所有信息
    public ArrayList<messageBean> queryAllMess() {
        SQLiteDatabase db = getReadableDatabase();
        ArrayList<messageBean> messList = new ArrayList<>();
        Cursor cursor = db.query(TABLE_MESSAGE, null, null, null, null, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                messList.add(new messageBean(cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getInt(4), cursor.getInt(5)));
            }
            cursor.close();
        }
        return messList;
    }

    // 根据分类id查询
    public ArrayList<messageBean> queryMessByClassId(int classId) {
        SQLiteDatabase db = getReadableDatabase();
        ArrayList<messageBean> messList = new ArrayList<>();
        Cursor cursor = db.query(TABLE_MESSAGE, null, "classification =" + classId + "", null, null, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                messList.add(new messageBean(cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getInt(4), cursor.getInt(5)));
            }
            cursor.close();
        }
        return messList;
    }

    // 根据body来删除
    public int deleteFromDbByBody(String body) {
        SQLiteDatabase db = getReadableDatabase();
        return db.delete(TABLE_MESSAGE, "body like ?", new String[]{body});
    }

    // 更新分类
    public int updateMessClass(messageBean messBean, int targetClassId) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("number", messBean.getNumber());
        values.put("name", messBean.getName());
        values.put("body", messBean.getBody());
        values.put("queryId", messBean.getQueryId());
        values.put("classification", targetClassId);
        return db.update(TABLE_MESSAGE, values, "body like ?", new String[]{messBean.getBody()});
    }

    // 加密条目插入
    public long insertMessEnc(messageBean messEncItem,String preBody) {
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.query(TABLE_MESS_ENC, new String[]{"prebody"}, "prebody=?", new String[]{preBody}, null, null, null);
        if (cursor.getCount() == 0) {
            ContentValues values = new ContentValues();
            values.put("number", messEncItem.getNumber());
            values.put("name", messEncItem.getName());
            values.put("prebody", messEncItem.getName());
            values.put("body", messEncItem.getBody());
            values.put("queryId", messEncItem.getQueryId());
            values.put("classification", messEncItem.getMessId());
            return db.insert(TABLE_MESS_ENC, null, values);
        }
        cursor.close();
        return -1;
    }

    // 解密并从表中删除
    public int deleteFromDbByEncBody(String body) {
        SQLiteDatabase db = getReadableDatabase();
        return db.delete(TABLE_MESS_ENC, "body like ?", new String[]{body});
    }

    // 查询所有加密条目
    public ArrayList<messageBean> queryAllEncMess() {
        SQLiteDatabase db = getReadableDatabase();
        ArrayList<messageBean> messList = new ArrayList<>();
        Cursor cursor = db.query(TABLE_MESS_ENC, null, null, null, null, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                messList.add(new messageBean(cursor.getString(1), cursor.getString(2), cursor.getString(4), cursor.getInt(5), cursor.getInt(6)));
            }
            cursor.close();
        }
        return messList;
    }
}
