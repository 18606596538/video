package com.example.dxq_musicplayer;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

public class VideoViewAdapter extends BaseAdapter {
    private List<String> videoList;
    private Context context;
    private HashMap<String, Bitmap> cache;

    public VideoViewAdapter(Context context, List<String> videoList) {
        this.context = context;
        this.videoList = videoList;
        cache = new HashMap<>();
    }

    @Override
    public int getCount() {
        return videoList.size();
    }

    @Override
    public String getItem(int position) {
        return videoList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.video_view_layout, parent, false);
            holder = new ViewHolder();
            holder.thumbnailImageView = convertView.findViewById(R.id.videoThumbnailImageView);
            holder.titleTextView = convertView.findViewById(R.id.videoTitleTextView);
            holder.durationTextView = convertView.findViewById(R.id.videoDurationTextView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        String videoPath = videoList.get(position);
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(context, Uri.parse(videoPath));
        holder.titleTextView.setText(getVideoFileName(videoPath));
        holder.durationTextView.setText(getVideoDuration(retriever));
        holder.thumbnailImageView.setImageBitmap(getVideoThumbnail(retriever));

        return convertView;
    }

    private String getVideoFileName(String videoPath) {
        return videoPath.substring(videoPath.lastIndexOf("/") + 1);
    }

    private String getVideoDuration(MediaMetadataRetriever retriever) {
        String duration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        long durationInMillis = Long.parseLong(duration);
        long minutes = (durationInMillis / 1000) / 60;
        long seconds = (durationInMillis / 1000) % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    private Bitmap getVideoThumbnail(MediaMetadataRetriever retriever) {
        return retriever.getFrameAtTime();
    }

    private static class ViewHolder {
        ImageView thumbnailImageView;
        TextView titleTextView;
        TextView durationTextView;
    }
}
