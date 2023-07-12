package com.example.dxq_musicplayer;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.TextureView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.VideoView;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;

public class VideoPlayer extends Activity {

    private SimpleExoPlayer exoPlayer;
    private PlayerView playerView;
    private GestureDetector gestureDetector;
    private float previousX;
    private float previousY;
    private float previousBrightness;
    private float currentPlaybackSpeed = 1.0f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        //初始化exo播放器
        exoPlayer = new SimpleExoPlayer.Builder(this).build();

        playerView = findViewById(R.id.player_view);
        playerView.setPlayer(exoPlayer);

        // 创建手势检测器
        gestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDoubleTap(MotionEvent e) {
                togglePlayPause();
                return true;
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                float deltaX = e2.getX() - previousX;
                float deltaY = e2.getY() - previousY;

                if (Math.abs(deltaX) > Math.abs(deltaY)) {
                    // 水平滑动，调节播放进度
                    adjustPlayProgress(deltaX);
                } else {
                    // 垂直滑动，调节声音和亮度
                    if (e1.getX() < playerView.getWidth() / 2) {
                        // 左侧滑动，调节亮度
                        adjustBrightness(deltaY);
                    } else {
                        // 右侧滑动，调节声音
                        adjustVolume(deltaY);
                    }
                }

                previousX = e2.getX();
                previousY = e2.getY();
                return true;
            }
        });


        playerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                gestureDetector.onTouchEvent(event);
                return true;
            }
        });

        //加载视频
        Intent intent = getIntent();
        String videoPath = intent.getStringExtra("video");

        MediaItem mediaItem = MediaItem.fromUri(videoPath);


        exoPlayer.setMediaItem(mediaItem);
        exoPlayer.setPlayWhenReady(true);
        exoPlayer.prepare();
/**
        // 设置倍速按钮点击事件
        Button btnSpeed05 = findViewById(R.id.btn_speed_05);
        Button btnSpeed10 = findViewById(R.id.btn_speed_10);
        Button btnSpeed15 = findViewById(R.id.btn_speed_15);
        Button btnSpeed20 = findViewById(R.id.btn_speed_20);

        btnSpeed05.setOnClickListener(view -> setPlaybackSpeed(0.5f));
        btnSpeed10.setOnClickListener(view -> setPlaybackSpeed(1.0f));
        btnSpeed15.setOnClickListener(view -> setPlaybackSpeed(1.5f));
        btnSpeed20.setOnClickListener(view -> setPlaybackSpeed(2.0f));**/

        //倍速按钮
        Button btnCustomButton = findViewById(R.id.btn_speed);
        btnCustomButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 在此处添加自定义按钮点击逻辑
                // 例如，处理特定操作或触发其他事件
            }
        });
    }

    private void setPlaybackSpeed(float speed) {
        currentPlaybackSpeed = speed;
        PlaybackParameters playbackParameters = new PlaybackParameters(speed);
        exoPlayer.setPlaybackParameters(playbackParameters);
    }
    private void togglePlayPause() {
        if (exoPlayer.isPlaying()) {
            exoPlayer.pause();
        } else {
            exoPlayer.play();
        }
    }

    private void adjustPlayProgress(float deltaX) {
        long currentPosition = exoPlayer.getCurrentPosition();
        long duration = exoPlayer.getDuration();
        float positionOffset = deltaX / playerView.getWidth();
        long seekPosition = (long) (currentPosition + positionOffset * duration);
        seekPosition = Math.max(0, Math.min(seekPosition, duration));
        exoPlayer.seekTo(seekPosition);
    }

    private void adjustVolume(float deltaY) {
        float currentVolume = exoPlayer.getVolume();
        float volumeOffset = -deltaY / playerView.getHeight();
        float newVolume = Math.max(0, Math.min(currentVolume + volumeOffset, 1));
        exoPlayer.setVolume(newVolume);
    }

    private void adjustBrightness(float deltaY) {
        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        float currentBrightness = layoutParams.screenBrightness;
        if (currentBrightness < 0) {
            try {
                currentBrightness = android.provider.Settings.System.getInt(getContentResolver(), android.provider.Settings.System.SCREEN_BRIGHTNESS) / 255f;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        float brightnessOffset = -deltaY / playerView.getHeight();
        float newBrightness = Math.max(0, Math.min(currentBrightness + brightnessOffset, 1));
        layoutParams.screenBrightness = newBrightness;
        getWindow().setAttributes(layoutParams);
    }


    @Override
    protected void onResume() {
        super.onResume();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    //在返回上级界面时，停止视频的播放
    @Override
    public void onBackPressed(){
        exoPlayer.stop();
        exoPlayer.release();
        super.onBackPressed();
    }
}