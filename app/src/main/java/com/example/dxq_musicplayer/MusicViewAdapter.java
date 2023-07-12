package com.example.dxq_musicplayer;


import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.RandomAccess;


public class MusicViewAdapter extends BaseAdapter {
    public class Holder{
        private ImageView image;
        private TextView title;
    }

    private List list ;
    private Context mContext;

    public MusicViewAdapter(Context context, List list){
        mContext = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        Holder holder = null;
        LayoutInflater inflater = LayoutInflater.from(mContext);
        if(view == null){
            holder = new Holder();
            view = inflater.inflate(R.layout.music_view_layout,null);
            holder.image = (ImageView)view.findViewById(R.id.detail_info_img);
            holder.title = (TextView)view.findViewById(R.id.music_base_info);
            view.setTag(holder);
        }else{
            holder = (Holder)view.getTag();
        }
        holder.title.setText((String)list.get(position));
        holder.image.setImageResource(android.R.drawable.ic_menu_info_details);
        holder.image.setOnClickListener(
                view1 -> {
                    Log.e("holder", "aaa");
                }
        );
        return view;
    }
}
