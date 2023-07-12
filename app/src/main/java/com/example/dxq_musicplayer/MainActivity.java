package com.example.dxq_musicplayer;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final int READ_EXTERNAL_STORAGE_PERMISSION_REQUEST = 1;
    private ArrayList<String> mediaFiles;
    private ListView listView;
    private ToggleButton toggleButton;
    int count_music = 0;
    int count_video = 0;
    private static String[] permissions = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = findViewById(R.id.listView);
        toggleButton = findViewById(R.id.switchAudioVideo);
        mediaFiles = new ArrayList<>();

        // 设置列表项的点击事件
        listView.setOnItemClickListener((adapterView, view, position, l) -> playMediaFile(position));

        // 设置切换显示音频和视频的按钮的点击事件
        toggleButton.setOnClickListener(
                view -> {
                    if (toggleButton.isChecked()) {
                        VideoViewAdapter adapter = new VideoViewAdapter(this, mediaFiles.subList(count_music, mediaFiles.size()));
                        listView.setAdapter(adapter);
                    } else {
                        MusicViewAdapter adapter = new MusicViewAdapter(this, mediaFiles.subList(0, count_music));
                        listView.setAdapter(adapter);
                    }
                }
        );

        requestReadExternalStoragePermission();
        requestWriteExternalStoragePermission();
    }

    // 请求读取外部存储权限
    private void requestReadExternalStoragePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    READ_EXTERNAL_STORAGE_PERMISSION_REQUEST);
        } else {
            loadMediaFiles();
        }
    }

    // 请求写入外部存储权限
    private void requestWriteExternalStoragePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    1);
        }
    }

    // 处理权限请求结果
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == READ_EXTERNAL_STORAGE_PERMISSION_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                loadMediaFiles();
            } else {
                Toast.makeText(this, "需要读取外部存储权限来加载媒体文件", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void loadMediaFiles() {
        // 扫描外部存储器上的媒体文件
        scanMediaFiles();

        // 在列表中显示媒体文件
        // 默认显示音频内容
        MusicViewAdapter adapter = new MusicViewAdapter(this, mediaFiles.subList(0, count_music));
        listView.setAdapter(adapter);
    }

    // 扫描媒体文件
    private void scanMediaFiles() {
        ContentResolver contentResolver = getContentResolver();

        //检索音频文件
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String selection = MediaStore.Audio.Media.IS_MUSIC + "!= 0";
        String sortOrder = MediaStore.Audio.Media.TITLE + " ASC";

        Cursor cursor = contentResolver.query(uri, null, selection, null, sortOrder);
        if (cursor != null && cursor.getCount() > 0) {

            while (cursor.moveToNext()) {
                String data = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                mediaFiles.add(data);
                ++count_music;
            }
        }
        if (cursor != null)
            cursor.close();


        //检索视频文件
        cursor = contentResolver.query(
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI, null, null, null,
                MediaStore.Video.Media.TITLE + " ASC");
        if (cursor != null && cursor.getCount() > 0) {

            while (cursor.moveToNext()) {
                String data = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA));
                mediaFiles.add(data);
                ++count_video;
            }
        }
        if (cursor != null)
            cursor.close();
    }


    // 跳转到相应的播放器界面
    private void playMediaFile(int position) {
        if(toggleButton.isChecked()){
            Intent intent = new Intent(MainActivity.this, VideoPlayer.class);
            intent.putExtra("video", mediaFiles.get(position + count_music));

            startActivity(intent);
        }
        else{
            Intent intent = new Intent(MainActivity.this, MusicPlayer.class);
            intent.putExtra("fileIndex", position);
            intent.putExtra("countMusic", count_music);
            intent.putStringArrayListExtra("mediaFiles", mediaFiles);

            startActivity(intent);
        }

    }

}
