package com.sunxiao.mathapplication;

import android.content.res.Configuration;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;



public class Main3Activity extends AppCompatActivity {
  //  private IjkPlayerView mPlayerView;
    private String VIDEO_URL ="http://pili-live-hls.nengapp.com/nengapp/test1.m3u8";
    private static final String IMAGE_URL = "http://vimg2.ws.126.net/image/snapshot/2016/11/I/M/VC62HMUIM.jpg";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);
        initView();
    }

    private void initView() {
      /*  mPlayerView = (IjkPlayerView) findViewById(R.id.player_view);
        mPlayerView.init()              // Initialize, the first to use
                .setTitle("Title")  	// set title
                .setSkipTip(1000*60*1)  // set the position you want to skip
                .enableOrientation()    // enable orientation
                //      .setVideoPath(VIDEO_URL)    // set video url
                .setVideoSource(null, VIDEO_URL,VIDEO_URL, VIDEO_URL, null) // set multiple video url
                .setMediaQuality(IjkPlayerView.MEDIA_QUALITY_HIGH)  // set the initial video url
                .enableDanmaku()        // enable Danmaku
                .setDanmakuSource(getResources().openRawResource(R.raw.bili)) // add Danmaku source, you need to use enableDanmaku() first
                .start();   // Start playing*/
    }

    @Override
    protected void onResume() {
        super.onResume();
        //mPlayerView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
       // mPlayerView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
      //  mPlayerView.onDestroy();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
       // mPlayerView.configurationChanged(newConfig);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
      /*  if (mPlayerView.handleVolumeKey(keyCode)) {
            return true;
        }*/
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onBackPressed() {
      /*  if (mPlayerView.onBackPressed()) {
            return;
        }*/
        super.onBackPressed();
    }
}
