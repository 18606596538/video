package com.example.dxq_musicplayer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class MusicPlayer extends AppCompatActivity {
    private int cur_playMusic_pos = 0;
    private int count_music = 0;
    private ArrayList<String> mediaFiles;
    private MediaPlayer mediaPlayer;
    private ImageButton prevMusicButton;
    private ImageButton nextMusicButton;
    private ImageButton musicPlayButton;
    private SeekBar seekBar;
    private ImageView musicDescribeImage;
    private TextView musicTitle;
    private boolean mediaPlayerExist = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_player);

        initMediaPlayer();

    }

    void initMediaPlayer(){
        Intent intent = getIntent();
        count_music = intent.getIntExtra("countMusic", 0);
        cur_playMusic_pos = intent.getIntExtra("fileIndex", 0);
        mediaFiles = intent.getStringArrayListExtra("mediaFiles");

        mediaPlayer = new MediaPlayer();
        prevMusicButton = findViewById(R.id.prevMusicButton);
        nextMusicButton = findViewById(R.id.nextMusicButton);
        musicPlayButton = findViewById(R.id.musicPlayButton);
        musicDescribeImage = findViewById(R.id.musicDescrImage);
        seekBar = findViewById(R.id.seekBar);
        musicTitle = findViewById(R.id.MusicTitle);

        musicTitle.setSelected(true);

        bindListenerOnItem();

        //开始播放
        playMusic();
    }

    //为组件绑定方法
    void bindListenerOnItem(){
        mediaPlayer.setOnPreparedListener(mp -> {
            // MediaPlayer准备完成后触发
            seekBar.setMax(mediaPlayer.getDuration());

            // 更新SeekBar的进度
            new Timer().scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    if(mediaPlayerExist && mediaPlayer.isPlaying())
                        seekBar.setProgress(mediaPlayer.getCurrentPosition());
                }
            }, 0, 1000); // 每秒更新一次进度
        });
        mediaPlayer.setOnCompletionListener(
                mediaPlayer -> {
                    nextMusic();
        });
        mediaPlayer.setOnErrorListener((mediaPlayer, i, i1) -> false);
        seekBar.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        if (fromUser) {
                            mediaPlayer.seekTo(progress);
                        }
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {

                    }
                }
        );
        musicPlayButton.setOnClickListener(
                view -> {
                    if(mediaPlayer.isPlaying()){
                        mediaPlayer.pause();
                        musicPlayButton.setImageResource(android.R.drawable.ic_media_play);
                    }
                     else{
                        mediaPlayer.start();
                        musicPlayButton.setImageResource(android.R.drawable.ic_media_pause);
                    }
                }
        );
        nextMusicButton.setOnClickListener(
                view -> {nextMusic();}
        );
        prevMusicButton.setOnClickListener(
                view -> {prevMusic();}
        );
    }

    //在返回上级界面时，停止音乐的播放
    @Override
    public void onBackPressed(){
        if(mediaPlayer!=null) {
            mediaPlayerExist = false;
            if(mediaPlayer.isPlaying())
                mediaPlayer.stop();
            mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer = null;
        }
        super.onBackPressed();
    }

    void nextMusic(){
        if(cur_playMusic_pos < count_music){
            cur_playMusic_pos = (cur_playMusic_pos + 1) % count_music;
        }
        mediaPlayer.reset();
        playMusic();
    }

    void prevMusic(){
        if(cur_playMusic_pos == 0)
            cur_playMusic_pos = count_music - 1;
        else
            cur_playMusic_pos--;
        mediaPlayer.reset();
        playMusic();
    }

    void playMusic(){
        if(cur_playMusic_pos < count_music && cur_playMusic_pos < mediaFiles.size()){
            try{
                String[] sp = mediaFiles.get(cur_playMusic_pos).split("/");
                musicTitle.setText(sp[sp.length - 1]);
                mediaPlayer.setDataSource(mediaFiles.get(cur_playMusic_pos));
                mediaPlayer.prepare();
                mediaPlayer.start();
            }
            catch (IOException e){
                e.printStackTrace();
            }
        }
    }
}