package com.sunxiao.mathapplication;

import android.Manifest;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.pili.pldroid.player.AVOptions;
import com.pili.pldroid.player.PLMediaPlayer;
import com.pili.pldroid.player.widget.PLVideoTextureView;
import com.sunxiao.mathapplication.controller.LoadingView;
import com.sunxiao.mathapplication.controller.MediaController;
import com.sunxiao.mathapplication.controller.NetworkUtils;

import java.util.Random;

import master.flame.danmaku.controller.DrawHandler;
import master.flame.danmaku.danmaku.model.BaseDanmaku;
import master.flame.danmaku.danmaku.model.DanmakuTimer;
import master.flame.danmaku.danmaku.model.IDanmakus;
import master.flame.danmaku.danmaku.model.android.DanmakuContext;
import master.flame.danmaku.danmaku.model.android.Danmakus;
import master.flame.danmaku.danmaku.parser.BaseDanmakuParser;
import master.flame.danmaku.ui.widget.DanmakuView;

public class LivePlayActivity extends MPermissionsActivity  {
    private PLVideoTextureView plVideoTextureView;
    private LoadingView progressBar;
    private Button mirrorButton, playButton;
    private boolean mirror, play;
    public final static int MEDIA_CODEC_SW_DECODE = 0;
    public final static int MEDIA_CODEC_HW_DECODE = 1;
    public final static int MEDIA_CODEC_AUTO = 2;

    public final static int PREFER_FORMAT_M3U8 = 1;
    public final static int PREFER_FORMAT_MP4 = 2;
    public final static int PREFER_FORMAT_FLV = 3;
    private int codec ;

    private int videoKind ;
    private String videoPath ;
    private ImageView coverView ;
    private DanmakuView danmakuView ;
    private boolean mIsShowDanmaku;
    private DanmakuContext mDanmakuContext;
    private BaseDanmakuParser parser = new BaseDanmakuParser() {
        @Override
        protected IDanmakus parse() {
            return new Danmakus();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //设置无标题

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);  //设置全屏
        setContentView(R.layout.activity_live_play);
        initIntent();
        initPersion();
        initView();
        initPreview();
        initOption();
        initVideoResource();

    }

    private void initIntent() {
        Intent intent = getIntent();
        videoKind = intent.getIntExtra("kind",1);
        videoPath = intent.getStringExtra("path");
    }

    private void initPersion() {
        requestPermission(new String[]{ Manifest.permission.WRITE_EXTERNAL_STORAGE ,
                Manifest.permission.READ_EXTERNAL_STORAGE}, 0x0001);
        Log.e("networktype","type::"+NetworkUtils.getNetWorkTypeName(this));
    }

    private void initOption() {
        AVOptions options = new AVOptions();

        // 解码方式:
        // codec＝AVOptions.MEDIA_CODEC_HW_DECODE，硬解
        // codec=AVOptions.MEDIA_CODEC_SW_DECODE, 软解
         codec=AVOptions.MEDIA_CODEC_AUTO; // 硬解优先，失败后自动切换到软解
        // 默认值是：MEDIA_CODEC_SW_DECODE
        options.setInteger(AVOptions.KEY_MEDIACODEC, codec);
        // 准备超时时间，包括创建资源、建立连接、请求码流等，单位是 ms
        // 默认值是：无
        options.setInteger(AVOptions.KEY_PREPARE_TIMEOUT, 10 * 1000);
        // 默认的缓存大小，单位是 ms
        // 默认值是：500
        options.setInteger(AVOptions.KEY_CACHE_BUFFER_DURATION, 500);
        // 最大的缓存大小，单位是 ms
        // 默认值是：2000
        options.setInteger(AVOptions.KEY_MAX_CACHE_BUFFER_DURATION, 2000);
        // 设置 DRM 密钥
        // byte[] key = {xxx, xxx, xxx, xxx, xxx ……};
       //  options.setByteArray(AVOptions.KEY_DRM_KEY, key);

        // 设置偏好的视频格式，设置后会加快对应格式视频流的加载速度，但播放其他格式会出错
      //  options.setInteger(AVOptions.KEY_PREFER_FORMAT, PREFER_FORMAT_MP4);
        options.setInteger(AVOptions.KEY_PREFER_FORMAT, videoKind);
        // 请在开始播放之前配置
        plVideoTextureView.setAVOptions(options);
    }
    //String path  = Environment.getExternalStorageDirectory().getPath() + "/DCIM/Camera/VID_20171209_103210.mp4";
    //String path = "http://pili-live-hls.nengapp.com/nengapp/test1.m3u8";
   // String path = "http://www.geidco.org/html/files/2017-09/24/20170924163609090756674.mp4";
    private void initVideoResource() {
        //先设置播放地址，在进行播放暂停的动作
      //  Log.e("livePLay","path:"+videoPath);
        //plVideoTextureView.setVideoPath("http://www.geidco.org/html/files/2017-09/24/20170924163609090756674.mp4");
        plVideoTextureView.setVideoPath(videoPath);
    }


    private void initView() {
        plVideoTextureView = (PLVideoTextureView) findViewById(R.id.player);
        progressBar = (LoadingView) findViewById(R.id.progressBar);
        mirrorButton = (Button) findViewById(R.id.mirror_button);
        playButton = (Button) findViewById(R.id.play_button);
        mirrorButton.setOnClickListener(l);
        playButton.setOnClickListener(l);
        //播放控制器
       // MediaController mController = new MediaController(this);
       // plVideoTextureView.setMediaController(mController);

        //设置加载视频进度
        plVideoTextureView.setBufferingIndicator(progressBar);
        coverView = (ImageView) findViewById(R.id.cover_view);
        plVideoTextureView.setCoverView(coverView);
        danmakuView = (DanmakuView) findViewById(R.id.danmaku_view);

        danmakuView.enableDanmakuDrawingCache(true);
        danmakuView.setCallback(new DrawHandler.Callback() {
            @Override
            public void prepared() {
                mIsShowDanmaku = true;
                danmakuView.start();
                generateSomeDanmaku();
            }

            @Override
            public void updateTimer(DanmakuTimer timer) {

            }

            @Override
            public void danmakuShown(BaseDanmaku danmaku) {

            }

            @Override
            public void drawingFinished() {

            }
        });

        mDanmakuContext = DanmakuContext.create();
        danmakuView.prepare(parser, mDanmakuContext);


        /*plVideoTextureView.setOnPreparedListener(this);
        plVideoTextureView.setOnPreparedListener(this);
        plVideoTextureView.setOnInfoListener(this);
        plVideoTextureView.setOnCompletionListener(this);
        plVideoTextureView.setOnVideoSizeChangedListener(this);
        plVideoTextureView.setOnErrorListener(this);*/
        //，
        plVideoTextureView.setOnPreparedListener(new PLMediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(PLMediaPlayer plMediaPlayer, int i) {
               plVideoTextureView.start();
            }
        });

        plVideoTextureView.setOnErrorListener(new PLMediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(PLMediaPlayer plMediaPlayer, int i) {
               // Log.e("listen","error:::i::"+i+"::plMediaPlay::"+plMediaPlayer.getDataSource());
                return false;

            }
        });

        plVideoTextureView.setOnInfoListener(new PLMediaPlayer.OnInfoListener() {
            @Override
            public boolean onInfo(PLMediaPlayer plMediaPlayer, int i, int i1) {
               // Log.e("listen","infor::"+i);
                return false;
            }
        });

        plVideoTextureView.setOnCompletionListener(new PLMediaPlayer.OnCompletionListener() {
            //播放完成
            @Override
            public void onCompletion(PLMediaPlayer plMediaPlayer) {
                //Log.e("listen","completion::"+plMediaPlayer);
            }
        });
        plVideoTextureView.setOnVideoSizeChangedListener(new PLMediaPlayer.OnVideoSizeChangedListener() {
            @Override
            public void onVideoSizeChanged(PLMediaPlayer plMediaPlayer, int i, int i1) {
               // Log.e("listen","videosize::::"+plMediaPlayer+"::i::"+i+":::i1::"+i1);
            }
        });



    }
    /**
     * 向弹幕View中添加一条弹幕
     * @param content       弹幕的具体内容
     * @param  withBorder   弹幕是否有边框
     */
    private void addDanmaku(String content, boolean withBorder) {
        BaseDanmaku danmaku = mDanmakuContext.mDanmakuFactory.createDanmaku(BaseDanmaku.TYPE_SCROLL_RL);
        danmaku.text = content;
        danmaku.padding = 5;
        danmaku.textSize = sp2px(20);
        danmaku.textColor = Color.WHITE;
        danmaku.setTime(danmakuView.getCurrentTime());
        if (withBorder) {
            danmaku.borderColor = Color.GREEN;
        }
        danmakuView.addDanmaku(danmaku);
    }
    /**
     * sp转px的方法。
     */
    public int sp2px(float spValue) {
        final float fontScale = getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    /**
     * 随机生成一些弹幕内容以供测试
     */
    private void generateSomeDanmaku() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(mIsShowDanmaku) {
                    int time = new Random().nextInt(300);
                    String content = "" + time + time;
                    addDanmaku(content, false);
                    try {
                        Thread.sleep(time);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    private View.OnClickListener l = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.play_button:
                    //Log.e("play","play::"+play);
                    if (play) {
                      //  Log.e("play","if::"+play);
                        plVideoTextureView.pause();
                        playButton.setText("播放");
                        play = false ;
                    } else {
                        //Log.e("play","else::"+play);
                        plVideoTextureView.start();
                        playButton.setText("暂停");

                        play = true ;
                    }
                    break;
                case R.id.mirror_button:
                    if (mirror) {
                        plVideoTextureView.setMirror(false);
                        mirrorButton.setText("镜面");
                        mirror = false ;
                    } else {
                        plVideoTextureView.setMirror(true);
                        mirrorButton.setText("非镜面");
                        mirror = true ;
                    }

                    break;
            }
        }
    };

    private void initPreview() {
        //设置画面预览模式
        // 原始尺寸
        //plVideoTextureView.setDisplayAspectRatio(PLVideoTextureView.ASPECT_RATIO_ORIGIN);
        //适应屏幕
       // plVideoTextureView.setDisplayAspectRatio(PLVideoTextureView.ASPECT_RATIO_FIT_PARENT);
        //全屏铺满
        plVideoTextureView.setDisplayAspectRatio(PLVideoTextureView.ASPECT_RATIO_PAVED_PARENT);
        //16:9
        //plVideoTextureView.setDisplayAspectRatio(PLVideoTextureView.ASPECT_RATIO_16_9);
        //4:3
        //plVideoTextureView.setDisplayAspectRatio(PLVideoTextureView.ASPECT_RATIO_4_3);
        //出门去停车场拿车，等他们顺便打开空调让车里温度上来。
    }
    @Override
    protected void onPause() {
        super.onPause();
        if (danmakuView != null && danmakuView.isPrepared()) {
            danmakuView.pause();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        plVideoTextureView.stopPlayback();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mIsShowDanmaku = false;
        if (danmakuView != null) {
            danmakuView.release();
            danmakuView = null;
        }
    }
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus && Build.VERSION.SDK_INT >= 19) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

}
