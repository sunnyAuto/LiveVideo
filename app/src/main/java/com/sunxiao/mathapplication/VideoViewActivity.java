package com.sunxiao.mathapplication;

import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.VideoView;

public class VideoViewActivity extends AppCompatActivity {
    private VideoView videoView ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_view);
        videoView = (VideoView) findViewById(R.id.video_view);
        String path  = Environment.getExternalStorageDirectory().getPath() + "/DCIM/Camera/VID_20171209_124253.mp4";
        videoView.setVideoPath(path);
        videoView.start();
    }
}
