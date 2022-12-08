package com.example.privacy;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.privacy.adapter.homeRecycleradapter;
import com.example.privacy.database.MyDatabaseHelper;
import com.example.privacy.utils.PaddingItemDecoration;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class homeFragViewpager2 extends Fragment {
    private List<album> albumList = new ArrayList<>();
    private List<album> newAlbums;
    private MyDatabaseHelper albumDataBase;
    private AlertDialog.Builder builder;
    private ArrayList<PhotoItem> deletePhotoItems;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        albumDataBase = new MyDatabaseHelper(getContext());
        albumList = albumDataBase.queryAllAlbum();
        if (albumList.size() == 0) {
            album albumNode1 = new album(null, "人物", R.drawable.group, 0);
            albumList.add(albumNode1);
            album albumNode2 = new album(null, "证件", R.drawable.businesscard, 1);
            albumList.add(albumNode2);
            album albumNode3 = new album(null, "文件", R.drawable.document, 2);
            albumList.add(albumNode3);
            album albumNode4 = new album(null, "其它", R.drawable.other, 3);
            albumList.add(albumNode4);
            albumDataBase.insertAlbum(albumNode1);
            albumDataBase.insertAlbum(albumNode2);
            albumDataBase.insertAlbum(albumNode3);
            albumDataBase.insertAlbum(albumNode4);
        } else {
            albumList.get(0).setResId(R.drawable.group);
            albumList.get(1).setResId(R.drawable.businesscard);
            albumList.get(2).setResId(R.drawable.document);
            albumList.get(3).setResId(R.drawable.other);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.homerecycleview2, container, false);
        init(view);
        return view;
    }

    //     RecyclerView容器测试
    private void init(View view) {
        RecyclerView recyclerView = view.findViewById(R.id.albumRecycler);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(view.getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        homeRecycleradapter myAdapter = new homeRecycleradapter(view.getContext(), albumList);
        myAdapter.setOnItemClickListener(new homeRecycleradapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Intent intent1 = new Intent(getActivity(), picRecyclerActivityOne.class);
                Bundle bundle1 = new Bundle();
                bundle1.putInt("albumId", position);
//                bundle1.putInt("albumNum",albumList.size());
                intent1.putExtras(bundle1);
                startActivity(intent1);
            }
        });
        myAdapter.setOnItemLongClickListener(new homeRecycleradapter.OnItemLongClickListener() {
            @Override
            public void onItemLongClick(View view, int position) {
                String albumName = albumList.get(position).getAlbum_name();
                int deleteId = albumList.get(position).getAlbumId();
                PopupMenu popupMenu = new PopupMenu(getContext(), view);
                popupMenu.getMenuInflater().inflate(R.menu.ablum_menu, popupMenu.getMenu());

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        if (menuItem.getItemId() == R.id.removeAlbumItem) {
                            if (position < 4) {
                                Toast.makeText(getContext(), "默认相册，不可删除！", Toast.LENGTH_SHORT).show();
                            } else {
                                // 若删除则，把对应的照片分类到其它 id:3
                                albumList.remove(position);
                                myAdapter.notifyItemRemoved(position);
                                albumDataBase.deleteFromTableTwoByName(albumName); // 从相册数据库中删除
                                deletePhotoItems = albumDataBase.queryFromDbById(deleteId);
                                moveAlbum(deletePhotoItems);
                            }
                        }
                        return false;
                    }
                });
                popupMenu.show();
            }
        });
        // 为增加相册按钮绑定事件
        FloatingActionButton floatingActionButton = view.findViewById(R.id.floatingBtn);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showInput(myAdapter);
            }
        });
        // 点击加入加密相册，显示信息
        FloatingActionButton floatingActionButton1 = view.findViewById(R.id.encryptActBtn);
        floatingActionButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent decryptIntent = new Intent(getActivity(), encryptActivity.class);
                startActivity(decryptIntent);
            }
        });

        recyclerView.addItemDecoration(new PaddingItemDecoration(getContext()));
        recyclerView.setAdapter(myAdapter);
    }

    public void showInput(homeRecycleradapter myAdapter) {
        EditText editText = new EditText(getContext());
        builder = new AlertDialog.Builder(getContext())
                .setTitle("请输入新相册名字")
                .setView(editText)
                .setPositiveButton("保存", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(getContext(), "输入内容为：" + editText.getText().toString()
                                , Toast.LENGTH_LONG).show();

                        album newAlbum = new album(null, editText.getText().toString(), R.drawable.unknown, albumList.size());
                        myAdapter.addAlbum(newAlbum);
                        long row = albumDataBase.insertAlbum(newAlbum);
                        if (row != -1) {
                            Toast.makeText(getContext(), "添加成功！", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getContext(), "添加失败！", Toast.LENGTH_SHORT).show();
                        }
                        myAdapter.notifyDataSetChanged();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(getContext(), "取消"
                                , Toast.LENGTH_LONG).show();
                    }
                });
        builder.create().show();
    }

    public void moveAlbum(ArrayList<PhotoItem> photoItems) {
        int res = -1;
        for (int i = 0; i < photoItems.size(); i++) {
            res = albumDataBase.updateDate(photoItems.get(i).getImage_url(), 3);
            if (res != -1) {
                Toast.makeText(getContext(), "成功移动" + (i + 1) + "张照片", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
