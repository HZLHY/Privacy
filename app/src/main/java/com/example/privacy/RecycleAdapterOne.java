package com.example.privacy;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class RecycleAdapterOne extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArrayList<PhotoItem> photoItemArrayList; // 包含照片路径的数组
    private Context myContext;
    private final int TYPE_EMPTY = 0;
    private final int TYPE_NORMAL = 1;
    // 图片长按测试
    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener{
        void onItemLongClick(View view,int position);
    }
    public void setOnItemClickListener(OnItemClickListener onItemClickListener){
        this.onItemClickListener = onItemClickListener;
    }
    @Override
    public int getItemViewType(int position) {
        if (photoItemArrayList.size()== 0) {  // 测试判断条件从null到size==0
            return TYPE_EMPTY;
        }
        return TYPE_NORMAL;
    }

    public RecycleAdapterOne(ArrayList<PhotoItem> photoItems, Context context) {
        this.photoItemArrayList = photoItems;
        this.myContext = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View photoView;
        if (viewType == TYPE_EMPTY) {
            photoView = View.inflate(myContext, R.layout.recycler_empty, null);
            return new RecyclerView.ViewHolder(photoView) {
            };
        } else {
            photoView = View.inflate(myContext, R.layout.pic_recylcer_item, null);
            MyViewHolder viewHolder = new MyViewHolder(photoView);
            return viewHolder;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        //先判断holder是否为自定义holder
        if (holder instanceof MyViewHolder) {
            MyViewHolder myViewHolder = (MyViewHolder) holder;
            PhotoItem photoItem = photoItemArrayList.get(position); // 获得传入list的其中一个
            ImageView imageView = myViewHolder.photoImageView;
            ViewGroup.LayoutParams layoutParams = myViewHolder.photoImageView.getLayoutParams();
            float itemWidth = (ScreenUtils.getScreenWidth(holder.itemView.getContext()) - 16 * 3) / 2;
            layoutParams.width = (int) itemWidth;
            float scale = (itemWidth + 0f) / photoItem.getWidth();
            layoutParams.height = (int)(photoItem.getHeight() * scale);
            myViewHolder.photoImageView.setLayoutParams(layoutParams);

            if(onItemClickListener!=null){
                myViewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {  // itemView 还是photoview？
                    @Override
                    public boolean onLongClick(View view) {
                        int livePosition = holder.getAdapterPosition();
                        onItemClickListener.onItemLongClick(myViewHolder.itemView,livePosition);  // 这里position有问题，先试试，等下去看视频
                        return false;
                    }
                });
            }
            // 有时间再搞了，目前能显示出来就算成功，哈哈哈哈
            Glide.with(myContext)
                    .load(photoItem.getImage_url())
                    .override(layoutParams.width, layoutParams.height)
                    .centerCrop()
                    .error(R.drawable.pic)
                    .into(imageView);


        }
    }

    @Override
    public int getItemCount() {
//        return photoItemArrayList == null ? 0 : photoItemArrayList.size();
        if (photoItemArrayList.size()==0) {
            return 1;
        }
        return photoItemArrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener { // 这个方法目的？
        public ImageView photoImageView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            photoImageView = itemView.findViewById(R.id.picRecyclerItem);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) { // 点击图片进入展示页面
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                Log.i("clickTest", "点击图片" + position);
            }
        }
    }

}
