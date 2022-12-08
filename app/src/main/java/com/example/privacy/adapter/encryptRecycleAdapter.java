package com.example.privacy.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.privacy.R;
import com.example.privacy.bean.encryptDbBean;
import com.example.privacy.database.MyDatabaseHelper;

import java.util.ArrayList;

public class encryptRecycleAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArrayList<encryptDbBean> encryptDataList;
    private Context mContext;
    private OnItemLongClickListener longClickListener;
    private MyDatabaseHelper encryptDataBase;
    private final int TYPE_EMPTY = 0;
    private final int TYPE_NORMAL = 1;

    public interface OnItemLongClickListener {
        void onItemLongClick(View view,int position);
    }

    public void setOnItemLongClickListener(OnItemLongClickListener longClickListener) {
        this.longClickListener = longClickListener;
    }

    public encryptRecycleAdapter(ArrayList<encryptDbBean> encryptDataList, Context mContext) {
        this.encryptDataList = encryptDataList;
        this.mContext = mContext;
    }

    @Override
    public int getItemViewType(int position) {
        if(encryptDataList.size()==0){
            return TYPE_EMPTY;
        }
        return TYPE_NORMAL;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View encryptView;
        if(viewType==TYPE_EMPTY){
            encryptView =View.inflate(mContext,R.layout.recycler_empty,null);
            return new RecyclerView.ViewHolder(encryptView){};
        }else {
            encryptView = View.inflate(mContext,R.layout.encrypt_recycle_item,null);
            encryptViewHolder viewHolder = new encryptViewHolder(encryptView);
            return viewHolder;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        // 判断是否属于自定义holder
        if(holder instanceof encryptViewHolder){
            encryptViewHolder viewHolder =(encryptViewHolder) holder;
            encryptDbBean encryptItem = encryptDataList.get(position);
            ImageView imageView = viewHolder.imageView;
            TextView textName = viewHolder.textName;
            TextView textId = viewHolder.textId;

            textName.setText(getName(encryptDataList.get(position).getImageUrl()));
            textId.setText(String.valueOf(encryptDataList.get(position).getClassId()));

            // 设置长按事件
            viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    int livePosition = viewHolder.getAdapterPosition();
                    if(longClickListener!=null){
                        longClickListener.onItemLongClick(view,livePosition);
                    }
                    return true;
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        if(encryptDataList.size()==0){
            return 1;
        }
        return encryptDataList.size();
    }
    public class encryptViewHolder extends RecyclerView.ViewHolder{
        public ImageView imageView;
        public TextView textName;
        public TextView textId;
        public encryptViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.encryptPic);
            textName = itemView.findViewById(R.id.encryptPicName);
            textId = itemView.findViewById(R.id.encryptPicId);
        }
    }
    public String getName(String path){
        int start = path.lastIndexOf("/");
        if(start!=-1){
            return path.substring(start+1);
        }else {
            return null;
        }
    }
}
