package com.example.privacy.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.privacy.R;
import com.example.privacy.album;
import com.example.privacy.database.MyDatabaseHelper;

import java.util.ArrayList;
import java.util.List;

public class homeRecycleradapter extends RecyclerView.Adapter<homeRecycleradapter.MyViweHolder> {
    private List<album> albumList;
    private Context mContext;
    private OnItemClickListener listener;
    private OnItemLongClickListener longClickListener;
    private MyDatabaseHelper albumDataBase;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }


    public interface OnItemLongClickListener {
        void onItemLongClick(View view, int position);
    }

    public void setOnItemLongClickListener(OnItemLongClickListener longClickListener) {
        this.longClickListener = longClickListener;
    }

    public homeRecycleradapter(Context context, List<album> albumList) {
        this.albumList = albumList;
        this.mContext = context;
    }

    @NonNull
    @Override
    public homeRecycleradapter.MyViweHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View albumView;
        albumView = LayoutInflater.from(mContext).inflate(R.layout.homelistitem2, parent, false);
        MyViweHolder viweHolder = new MyViweHolder(albumView);
        return viweHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull homeRecycleradapter.MyViweHolder viewHolder, int position) {
        album albumItem = albumList.get(position);
        viewHolder.textView.setText(albumList.get(position).getAlbum_name());
        viewHolder.imageView.setImageResource(albumList.get(position).getResId());
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int livePosition = viewHolder.getAdapterPosition();
                if (listener != null) {
                    listener.onItemClick(livePosition);
                }
            }
        });

        viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                int livePosition = viewHolder.getAdapterPosition();
                if (longClickListener != null) {
                    longClickListener.onItemLongClick(v, livePosition);
                }
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        if (albumList.size() != 0) {
            return albumList.size();
        }
        return 0;
    }

    public class MyViweHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public TextView textView;

        public MyViweHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.album1Fir_image);
            textView = itemView.findViewById(R.id.album1Name);
        }
    }

    public void addAlbum(album newAlbum) {
        albumList.add(newAlbum);
    }
}
