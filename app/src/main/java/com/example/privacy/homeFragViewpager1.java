package com.example.privacy;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.privacy.adapter.messHomeRecyAdapter;
import com.example.privacy.adapter.messRecyAdapter;
import com.example.privacy.bean.messageBean;
import com.example.privacy.bean.messageSetBean;
import com.example.privacy.database.MyDatabaseHelper;
import com.example.privacy.utils.LRTagDecoration;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class homeFragViewpager1 extends Fragment {
    private ArrayList<messageSetBean> messageSets=new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        messageSets.add(new messageSetBean("工作短信",0,R.drawable.work));
        messageSets.add(new messageSetBean("生活短信",1,R.drawable.personal));
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.homefviewpager1, container, false);
        init(view);
        return view;
    }

    private void init(View view) {
        RecyclerView recyclerView = view.findViewById(R.id.messageRecycler);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(view.getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);

        messHomeRecyAdapter myAdapter = new messHomeRecyAdapter(messageSets, view.getContext());

        myAdapter.setOnItemClickListener(new messHomeRecyAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Intent intent = new Intent(getActivity(),messRecyActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt("messageSetId",position);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        // 为浮动按钮绑定点击事件，位置有问题，先不搞了
//        FloatingActionButton floatingActionButton1 = view.findViewById(R.id.messSendBtn);
//        floatingActionButton1.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Toast.makeText(getContext(), "点击扫描或发送", Toast.LENGTH_SHORT).show();
//                PopupMenu popupMenu = new PopupMenu(getContext(),getView());
//                popupMenu.getMenuInflater().inflate(R.menu.mess_item_menu,popupMenu.getMenu());
//                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
//                    @Override
//                    public boolean onMenuItemClick(MenuItem menuItem) {
//                        switch (menuItem.getItemId()){
//                            case R.id.messGetClassBtn:
//                                Toast.makeText(getContext(), "获取分类", Toast.LENGTH_SHORT).show();
//                                break;
//                        }
//                        return false;
//                    }
//                });
//                popupMenu.show();
//            }
//        });

        FloatingActionButton floatingActionButton2 = view.findViewById(R.id.messEncryptActBtn);
        floatingActionButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(),messEncActivity.class);
                startActivity(intent);
                Toast.makeText(getContext(), "进入加密信息列表", Toast.LENGTH_SHORT).show();
            }
        });
        recyclerView.addItemDecoration(new LRTagDecoration(getContext()));
        recyclerView.setAdapter(myAdapter);
    }
}

