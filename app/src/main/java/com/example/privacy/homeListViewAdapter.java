package com.example.privacy;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class homeListViewAdapter extends BaseAdapter {
    private List<album> albumList;
    private Context context;

    public homeListViewAdapter(List<album> albumList, Context context) {
        this.albumList = albumList;
        this.context = context;
    }

    @Override
    public int getCount() {
        if (albumList == null) {
            return 0;
        }
        return albumList.size();
    }

    @Override
    public Object getItem(int position) {
        if (albumList == null) {
            return null;
        }
        return albumList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();  // 防止重复通过id查找
            convertView = LayoutInflater.from(context).inflate(R.layout.homelistitem2, null);
            viewHolder.textView = convertView.findViewById(R.id.album1Name);
            viewHolder.imageView = convertView.findViewById(R.id.album1Fir_image);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.textView.setText(albumList.get(position).getAlbum_name());
        viewHolder.imageView.setImageResource(albumList.get(position).getAlbumId());

        return convertView;
    }

    private final class ViewHolder {
        TextView textView;
        ImageView imageView;
    }
    public void addAlbum(){
        albumList.add(new album(null,"其它",R.drawable.community,R.drawable.community));
        notifyDataSetChanged();
    }
    public void deleteAlbum(){
        albumList.remove(albumList.size()-1);
        notifyDataSetChanged();
    }
}
