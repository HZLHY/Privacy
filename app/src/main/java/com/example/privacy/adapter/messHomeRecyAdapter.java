package com.example.privacy.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.privacy.R;
import com.example.privacy.bean.messageSetBean;

import java.util.ArrayList;

public class messHomeRecyAdapter extends RecyclerView.Adapter<messHomeRecyAdapter.MyViewHolder> {
    private ArrayList<messageSetBean> messageSets;
    private Context mContext;
    private OnItemClickListener listener;
    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public messHomeRecyAdapter(ArrayList<messageSetBean> messageSets, Context mContext) {
        this.messageSets = messageSets;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public messHomeRecyAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View messSetsView;
        messSetsView = LayoutInflater.from(mContext).inflate(R.layout.mess_home_item,parent,false);
        MyViewHolder viewHolder = new MyViewHolder(messSetsView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull messHomeRecyAdapter.MyViewHolder holder, int position) {
        messageSetBean messSetItem = messageSets.get(position);
        holder.textSetName.setText(messSetItem.getSetName());
        holder.imageView.setImageResource(messSetItem.getResId());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int livePosition = holder.getAdapterPosition();
                if(listener!=null){
                    listener.onItemClick(livePosition);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return messageSets.size()==0?0:messageSets.size();
    }
    public class MyViewHolder extends RecyclerView.ViewHolder{
        public ImageView imageView;
        public TextView textSetName;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.messHomeImage);
            textSetName = itemView.findViewById(R.id.messHomeName);
        }
    }
}
