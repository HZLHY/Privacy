package com.example.privacy.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.privacy.R;
import com.example.privacy.bean.messageBean;

import java.util.ArrayList;

public class messRecyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArrayList<messageBean> messList;
    private Context mContext;
    private OnItemLongClickListener longClickListener;
    private final int TYPE_EMPTY = 0;
    private final int TYPE_NORMAL = 1;

    public interface OnItemLongClickListener {
        void onItemLongClick(View view, int position);
    }

    public void setOnItemLongClickListener(OnItemLongClickListener longClickListener) {
        this.longClickListener = longClickListener;
    }

    public messRecyAdapter(ArrayList<messageBean> messList, Context mContext) {
        this.messList = messList;
        this.mContext = mContext;
    }

    @Override
    public int getItemViewType(int position) {
        if (messList.size() == 0) {
            return TYPE_EMPTY;
        }
        return TYPE_NORMAL;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View messReView;
        if (viewType == TYPE_EMPTY) {
            messReView = View.inflate(mContext, R.layout.recycler_empty, null);
            return new RecyclerView.ViewHolder(messReView) {
            };
        } else {
            messReView = View.inflate(mContext, R.layout.mess_recy_item, null);
            messViewHolder viewHolder =new messViewHolder(messReView);
            return viewHolder;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof messViewHolder){
            messViewHolder viewHolder = (messViewHolder) holder;
            messageBean messItem = messList.get(position);
            ImageView imageView = viewHolder.imageView;
            TextView textNumber = viewHolder.textNumber;
            TextView textName = viewHolder.textName;
            TextView textBody = viewHolder.textBody;

            textNumber.setText(messList.get(position).getNumber());
            textBody.setText(messList.get(position).getBody()); //要提取应该显示的长度文字内容，以后再改了……
            textName.setText(messList.get(position).getName());

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
        if (messList.size() == 0) {
            return 1;
        }
        return messList.size();
    }

    public class messViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public TextView textNumber;
        public TextView textName;
        public TextView textBody;

        public messViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.messPic);
            textNumber = itemView.findViewById(R.id.messNumber);
            textName = itemView.findViewById(R.id.messName);
            textBody = itemView.findViewById(R.id.messBody);
        }
    }
}
