package com.sunxiao.mathapplication;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.database.ContentObserver;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.OrientationEventListener;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.pili.pldroid.player.AVOptions;
import com.pili.pldroid.player.PLMediaPlayer;
import com.pili.pldroid.player.PlayerState;
import com.pili.pldroid.player.widget.PLVideoTextureView;
import com.sunxiao.mathapplication.Utils.AnimationUtil;
import com.sunxiao.mathapplication.Utils.NetUtils;
import com.sunxiao.mathapplication.Utils.ScreenSwitchUtils;
import com.sunxiao.mathapplication.View.BrightnessHelper;
import com.sunxiao.mathapplication.View.CommentDialog;
import com.sunxiao.mathapplication.View.ShowChangeLayout;
import com.sunxiao.mathapplication.View.VideoGestureRelativeLayout;
import com.sunxiao.mathapplication.controller.DensityUtil;
import com.sunxiao.mathapplication.controller.LoadingView;
import com.sunxiao.mathapplication.manager.FloatWindowManager;
import com.sunxiao.mathapplication.manager.FloatWindowPermissionChecker;

import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class Main2Activity extends AppCompatActivity implements ViewTreeObserver.OnGlobalLayoutListener {
    public static PLVideoTextureView vView;
    private PLMediaPlayer plMediaPlayer;

    private ImageView play, full, dan, back;
    //private String videoPath ;
    private LoadingView loading;
    public static int codec;
    private int landPort;
    private RelativeLayout backLayout, danLayout, landLayout, fullLayout;
    private VideoGestureRelativeLayout control;
    private int clicked;
    private boolean isShow = true;
    private RelativeLayout btnLayout, content;
    private int screenWidth, screenHeight;
    public static int videoKind;
    public static String videoPath;
    private SeekBar seekBar;
    private Timer timer;
    private TimerTask task;
    private int maxVolume = 0;
    private int oldVolume = 0;
    private int newProgress = 0, oldProgress = 0;
    private BrightnessHelper mBrightnessHelper;
    private float brightness = 1;
    private Window mWindow;
    private WindowManager.LayoutParams mLayoutParams;
    private ShowChangeLayout scl;
    private AudioManager mAudioManager;
    private boolean isLeft = false;
    private boolean isMoved = false;
    private TextView title;
    private ImageView ad_video, ad_paly;
    private String adVideoPath;
    private boolean isLive;
    private int played = 0;
    private TextView totalTime, currentTime;
    public static String result = "";
    private CommentDialog commentDialog;
    private float startX, startY, endX, endY;
    private ScreenSwitchUtils instance;
    private MyOrientoinListener myOrientoinListener;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Button startFlow, startFlow2;
    public static long currentP;
    private ImageView iv, screenLock;
    private int firstPlay = 0;
    private int stopStatus = 0;
    private long max = 0;
    private TextView changeClarity;
    private RelativeLayout shareLayout;
    private LinearLayout changeClarityLayout;
    private TextView lowClarity, midClarity, highClarity;
    private ImageView controllPLay;
    private NetChangeReceiver netChangeReceiver;
    private RelativeLayout replayLayout, netWarnLayout;//重新播放，网络变化提醒
    private TextView warnTxt, warnPlay, replayTxt;
    private ImageView replayBtn;
    //private boolean isLive ;
    private boolean isLocked;
    private boolean isMobile;//移动网络
    private boolean clarityShow; //切换清晰度显示
    private List<ClarityBean> list = new ArrayList<>();
    private RecyclerView rView ;
    private RecyclerGridViewAdapter recyclerGridViewAdapter ;
    private String[] data = {"能见会议直播视频一", "能见会议直播视频一", "能见会议直播视频一", "能见会议直播视频一",
            "能见会议直播视频一", "能见会议直播视频一", "能见会议直播视频一"};
    private int[] imgdata = {R.drawable.grsm, R.drawable.grsm, R.drawable.grsm, R.drawable.grsm,
            R.drawable.grsm, R.drawable.grsm, R.drawable.grsm,};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        myOrientoinListener = new MyOrientoinListener(this);

        myOrientoinListener.enable();
        getContentResolver().registerContentObserver(Settings.System.getUriFor("navigationbar_is_min"), true, contentObserver);
        // ScreenImproveUtils.assistActivity(findViewById(R.id.content));

        /**获取屏幕的宽高**/
        //  DisplayMetrics  dm = new DisplayMetrics();

        // getWindowManager().getDefaultDisplay().getMetrics(dm);
        //DisplayMetrics metrics = getResources().getDisplayMetrics();

        screenHeight = getRealHeight(this, 0);

        screenWidth = getRealHeight(this, 1);
        //初始化获取音量属性
        mAudioManager = (AudioManager) getSystemService(Service.AUDIO_SERVICE);
        maxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);

        //初始化亮度调节
        mBrightnessHelper = new BrightnessHelper(this);

        //下面这是设置当前APP亮度的方法配置
        mWindow = getWindow();
        mLayoutParams = mWindow.getAttributes();
        brightness = mLayoutParams.screenBrightness;
        initView();
        initSeekBar();
        initIntent();
        initOption();
        registerNetReceiver();
    }

    private boolean isUser = false;

    @Override
    protected void onResume() {
        super.onResume();
        Log.e("MAin2Activity", "onResume");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.e("MAin2Activity", "onStart");
    }

    private void initSeekBar() {
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (firstPlay == 1) {
                    if (vView.isPlaying()) {
                        vView.seekTo(seekBar.getProgress());
                        updateCurrrent();
                    } else {
                        isUser = true;
                        vView.start();
                        play.setImageResource(R.drawable.jz_pause_normal);
                        played = 1;
                        updateCurrrent();

                    }
                } else {
                    // notStartSeek();
                    //Log.e("notstart","progress::"+seekBar.getProgress()+":::current::"+vView.getCurrentPosition());
                    //currentP = seekBar.getProgress();
                    //notStartSeek();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                if (timer != null) {
                    timer.cancel();
                    timer = null;
                }
                if (task != null) {
                    task.cancel();
                    task = null;
                }
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, final int progress,
                                          boolean fromUser) {

                if (!fromUser) {
                    return;
                } else {
                    currentTime.post(new Runnable() {
                        @Override
                        public void run() {
                            currentTime.setText(stringForTime(progress));
                        }
                    });
                }


            }
        });
    }


    private void initIntent() {
        Intent intent = getIntent();
        list = (List<ClarityBean>)intent.getSerializableExtra("url");
        Log.e("lllllll","size::"+intent.getSerializableExtra("url"));
        /*videoKind = intent.getIntExtra("kind", 1);
        videoPath = intent.getStringExtra("path");
        vView.setVideoPath(videoPath);*/
        videoKind = list.get(0).getIsLive();
        videoPath = list.get(0).getVideoPath();
        changeClarity.setText(list.get(0).getClarityKinds());
        vView.setVideoPath(videoPath);
        getDiffentClarity();
        vView.setDisplayAspectRatio(PLVideoTextureView.ASPECT_RATIO_FIT_PARENT);
        if (videoKind == 1) {
            isLive = true;
            title.setText("能见直播，能源行业最权威的媒体");
        } else if (videoKind == 2) {
            isLive = false;
            title.setText("mp4视频播放中。。。。");
        }
        if (intent.getStringExtra("currentPosition") != null) {

            currentP = Long.parseLong(intent.getStringExtra("currentPosition"));

        } else {

        }

    }
    private void getDiffentClarity(){//获取多个清晰度的视频，并添加切换清晰度按钮
        if (list.size()>0){
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(0,10,0,10);//4个参数按顺序分别是左上右下
            for (int i = 0; i < list.size(); i++) {
                TextView tv = new TextView(this);
                tv.setLayoutParams(layoutParams);
                tv.setText(list.get(i).getClarityKinds());
                tv.setTextColor(Color.parseColor("#ffffff"));
                tv.setPadding(20 ,10 ,20,10);
                if (list.get(i).getDefaultClarity() == 1){
                    changeClarity.setText(list.get(i).getClarityKinds());
                    Drawable drawable = ContextCompat.getDrawable(this ,R.drawable.background_textview);
                    tv.setBackground(drawable);
                }
                changeClarityLayout.addView(tv);
            }
        }

        for (int j = 0 ; j < changeClarityLayout.getChildCount(); j++) {
            final int finalJ = j;
            changeClarityLayout.getChildAt(j).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.e("finalj","j::"+finalJ);
                    Drawable drawable = ContextCompat.getDrawable(Main2Activity.this ,R.drawable.background_textview);
                    v.setBackground(drawable);
                    for (int i = 0; i < changeClarityLayout.getChildCount(); i++) {
                        if (finalJ != i){
                            /*Drawable drawableNull = ContextCompat.getDrawable(Main2Activity.this ,null);
                            v.setBackground(drawable);*/
                            changeClarityLayout.getChildAt(i).setBackground(null);
                        }
                    }
                    changClarity(finalJ);
                }
            });
        }
    }
    private void changClarity(int i){
        currentP =  vView.getCurrentPosition();
        vView.setVideoPath(list.get(i).getVideoPath());
        vView.start();
        new Handler().postDelayed(new Runnable(){
            public void run() {
                //execute the task
                Log.e("handler","延迟执行");
                vView.seekTo(Main2Activity.currentP);
            }
        }, 500);
    }

    private void initOption() {
        AVOptions options = new AVOptions();

        // 解码方式:
        // codec＝AVOptions.MEDIA_CODEC_HW_DECODE;//硬解
        codec = AVOptions.MEDIA_CODEC_SW_DECODE;// 软解
        //codec=AVOptions.MEDIA_CODEC_AUTO; // 硬解优先，失败后自动切换到软解
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
        //options.setInteger(AVOptions.KEY_PREFER_FORMAT, 2);
        options.setInteger(AVOptions.KEY_PREFER_FORMAT, videoKind);
        // 请在开始播放之前配置
        vView.setAVOptions(options);

    }

    private void initView() {
        vView = (PLVideoTextureView) findViewById(R.id.videoPlayer);
        vView.setOnClickListener(l);
        /*ImageView iv = new ImageView(this);
        iv.setImageResource(R.drawable.grsm);*/
        iv = (ImageView) findViewById(R.id.iv);
        vView.setCoverView(iv);
        play = (ImageView) findViewById(R.id.play_button);
        play.setOnClickListener(l);
        controllPLay = (ImageView) findViewById(R.id.app_video_play);//控制条暂停播放按钮
        controllPLay.setOnClickListener(l);
        full = (ImageView) findViewById(R.id.full);
        full.setOnClickListener(l);
        dan = (ImageView) findViewById(R.id.dan);
        dan.setOnClickListener(l);
        back = (ImageView) findViewById(R.id.back);
        back.setOnClickListener(l);
        changeClarity = (TextView) findViewById(R.id.switch_clarity_button);
        changeClarity.setOnClickListener(l);
        totalTime = (TextView) findViewById(R.id.current_total_time);
        currentTime = (TextView) findViewById(R.id.current_play_time);
        loading = (LoadingView) findViewById(R.id.progressBar);
        vView.setBufferingIndicator(loading);
        /*videoPath = Environment.getExternalStorageDirectory().getPath() + "/DCIM/Camera/VID_20171209_103210.mp4";
        vView.setVideoPath(videoPath);*/
        vView.setOnPreparedListener(new PLMediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(PLMediaPlayer plMediaPlayer, int i) {
                //视频开启播放后, 触发的监听器
                max = vView.getDuration();
                totalTime.setText(stringForTime(max));
                seekBar.setMax((int) max);

                if (currentP != 0) {
                    firstPlay = 1;
                    if (!vView.isPlaying()) {
                        play.setImageResource(R.drawable.jz_pause_normal);
                        vView.start();
                        played = 1;
                        new Handler().postDelayed(new Runnable() {
                            public void run() {
                                //execute the task
                                Log.e("handler", "延迟执行");
                                vView.seekTo(currentP);
                            }
                        }, 500);
                        updateCurrrent();

                        updateControl();
                        //  isShow = 1;
                    }
                }

            }
        });
        vView.setOnCompletionListener(new PLMediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(PLMediaPlayer plMediaPlayer) {
                 Log.e("complete","complete");
                //完成播放
                //  play.setImageResource(R.drawable.jz_play_normal);
                updatePausePlay();
                updateReplay();
            }
        });
        vView.setOnSeekCompleteListener(new PLMediaPlayer.OnSeekCompleteListener() {
            @Override
            public void onSeekComplete(PLMediaPlayer plMediaPlayer) {

            }
        });
        vView.setOnInfoListener(new PLMediaPlayer.OnInfoListener() {
            @Override
            public boolean onInfo(PLMediaPlayer plMediaPlayer, int i, int i1) {

                if (plMediaPlayer.getPlayerState() == PlayerState.PLAYING) {
                    if (isUser == true) {
                        vView.seekTo(seekBar.getProgress());
                        isUser = false;
                    }
                }
                return false;
            }
        });
        vView.setOnErrorListener(new PLMediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(PLMediaPlayer plMediaPlayer, int i) {
                //Log.e("INfor","plMediaPLayer:"+plMediaPlayer.getPlayerState()+":::ERROR:"+i);
                return false;
            }
        });
        fullLayout = (RelativeLayout) findViewById(R.id.full_layout);
        content = (RelativeLayout) findViewById(R.id.content);
        control = (VideoGestureRelativeLayout) findViewById(R.id.control_layout);
        control.setOnClickListener(l);
        backLayout = (RelativeLayout) findViewById(R.id.back_layout);
        danLayout = (RelativeLayout) findViewById(R.id.dan_layout);
        landLayout = (RelativeLayout) findViewById(R.id.screen_full_layout);
        seekBar = (SeekBar) findViewById(R.id.seekBar1);
        scl = (ShowChangeLayout) findViewById(R.id.show_change);
        title = (TextView) findViewById(R.id.video_title);
        // ad_video = (ImageView) findViewById(R.id.ad_video);
        // ad_paly = (ImageView) findViewById(R.id.ad_video_play);
//        ad_paly.setOnClickListener(l);

        // control.setVideoGestureListener(this);
        //初始化亮度调节
        // mBrightnessHelper = new BrightnessHelper(this);
        control.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (firstPlay == 1) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            //每次按下的时候更新当前亮度和音量，还有进度
                            //  oldProgress = newProgress;
                            Log.e("isShow", "down::" + isShow);
                            oldProgress = seekBar.getProgress();
                            // Log.e("progress","oldProgress::"+oldProgress+":::sekbar::"+seekBar.getProgress()+"max:::"+seekBar.getMax());
                            oldVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                            brightness = mLayoutParams.screenBrightness;
                            if (brightness == -1) {
                                //一开始是默认亮度的时候，获取系统亮度，计算比例值
                                brightness = mBrightnessHelper.getBrightness() / 255f;
                            }

                            startX = event.getX();
                            startY = event.getY();
                            if (startX < control.getWidth() / 2) {
                                //手势布局左侧
                                isLeft = true;
                            } else {
                                //手势布局右侧
                                isLeft = false;
                            }

                            break;
                        case MotionEvent.ACTION_MOVE:
                            if (!isLocked) {
                                //判断上下还是左右
                                if (Math.abs(event.getY() - startY) > 30) {
                                    isMoved = true;
                                    if (isLeft) {
                                        //调亮度
                                        if ((event.getY() - startY) > 0) {
                                            //升高
                                            controlLight(startY - event.getY());
                                        } else {
                                            //降低
                                            controlLight(startY - event.getY());
                                        }
                                    } else {
                                        //调声音
                                        if ((event.getY() - startY) > 0) {
                                            //升高
                                            controlVoice(startY - event.getY());
                                        } else {
                                            //降低
                                            controlVoice(startY - event.getY());
                                        }
                                    }
                                } else {
                                    if (isLive == false) {
                                        if (Math.abs(event.getX() - startX) > 10) {
                                            isMoved = true;
                                            controlProgress(event.getX() - startX);
                                        }
                                    }
                                }
                            } else {

                            }
                            break;
                        case MotionEvent.ACTION_UP:
                            if (!clarityShow) {
                                if (!isMoved) {
                                    if (isShow) {
                                        updateControl();
                                        handler.removeCallbacks(runnable);
                                    } else {
                                        //隐藏
                                        updateControl();
                                        handler.postDelayed(runnable, 3000);//每三秒执行一次runnable.
                                    }
                                } else {
                                    isMoved = false;
                                    updateControl();
                                }
                            } else {
                                changeClarityLayout.setVisibility(View.GONE);
                                clarityShow = false;
                            }
                            break;
                    }
                    return true;
                } else {
                    return false;
                }
            }
        });

        //swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.refresh);
        //startFlow = (Button) findViewById(R.id.start_flow);
        // startFlow.setOnClickListener(l);

        startFlow2 = (Button) findViewById(R.id.start_flow2);
        startFlow2.setOnClickListener(l);

        shareLayout = (RelativeLayout) findViewById(R.id.layout_share_to);
        changeClarityLayout = (LinearLayout) findViewById(R.id.layout_change_clarity);
        /*lowClarity = (TextView) findViewById(R.id.low_clarity);
        lowClarity.setOnClickListener(l);
        midClarity = (TextView) findViewById(R.id.middle_clarity);
        midClarity.setOnClickListener(l);
        highClarity = (TextView) findViewById(R.id.high_clarity);
        highClarity.setOnClickListener(l);*/
        screenLock = (ImageView) findViewById(R.id.lock_touch);
        screenLock.setOnClickListener(l);


        warnTxt = (TextView) findViewById(R.id.warn_text);
        warnPlay = (TextView) findViewById(R.id.warn_play_button);
        warnPlay.setOnClickListener(l);
        replayTxt = (TextView) findViewById(R.id.replay_text);
        replayBtn = (ImageView) findViewById(R.id.replay_button);
        replayBtn.setOnClickListener(l);

        netWarnLayout = (RelativeLayout) findViewById(R.id.net_changed_warn);
        replayLayout = (RelativeLayout) findViewById(R.id.replay_layout);
        rView = (RecyclerView) findViewById(R.id.relative_video_recyclerview);
        GridLayoutManager mgr = new GridLayoutManager(this, 2);
        rView.setLayoutManager(mgr);
        recyclerGridViewAdapter = new RecyclerGridViewAdapter(this, data, imgdata);
        rView.setAdapter(recyclerGridViewAdapter);
        onRecyclerItemClickListener();
    }
    private void onRecyclerItemClickListener() {
        recyclerGridViewAdapter.setOnRecyclerViewItemListener(new RecyclerGridViewAdapter.OnRecyclerViewItemListener() {
            @Override
            public void onItemClickListener(View view, int position) {
                Toast.makeText(Main2Activity.this, "onClick:" + position, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onItemLongClickListener(View view, int position) {
                Toast.makeText(Main2Activity.this, "onLongClick:" + position, Toast.LENGTH_SHORT).show();
            }
        });
    }

    //获取有几种不同的清晰度
    private void setClartityBtnText() {

    }
    private void updateReplay(){
        replayLayout.setVisibility(View.VISIBLE);
        if (isShow){
            updateControl();
        }
    }

    private void updateCurrrent() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        if (task != null) {
            task.cancel();
            task = null;
        }
        timer = new Timer();
        task = new TimerTask() {
            @Override
            public void run() {
                // Log.e("timertask","updatecu"+vView.getCurrentPosition()+"++++percent:::"+(vView.getBufferPercentage()*max/100)+":::max::"+max);
                seekBar.setProgress((int) vView.getCurrentPosition());
                currentTime.post(new Runnable() {
                    @Override
                    public void run() {
                        currentTime.setText(stringForTime(vView.getCurrentPosition()));
                    }
                });

                seekBar.setSecondaryProgress((int) (vView.getBufferPercentage() * max / 100));
                // Log.e("timertask","updatecu"+vView.getCurrentPosition()+"===percentget:::"+seekBar.getSecondaryProgress());

            }
        };
        timer.schedule(task, 0, 1000);
    }

    /**
     * 把毫秒转换成：1:20:30这里形式
     *
     * @param timeMs
     * @return
     */
    public String stringForTime(long timeMs) {
        long totalSeconds = timeMs / 1000;
        long seconds = totalSeconds % 60;
        long minutes = (totalSeconds / 60) % 60;
        long hours = totalSeconds / 3600;
        mFormatBuilder.setLength(0);
        if (hours > 0) {
            return mFormatter.format("%d:%02d:%02d", hours, minutes, seconds).toString();
        } else {
            return mFormatter.format("%02d:%02d", minutes, seconds).toString();
        }
    }

    private StringBuilder mFormatBuilder = new StringBuilder();
    private Formatter mFormatter = new Formatter(mFormatBuilder, Locale.getDefault());

    private void controlLight(float distance) {

        float newBrightness = (distance) / control.getHeight();
        newBrightness += brightness;
        if (newBrightness < 0) {
            newBrightness = 0;
        } else if (newBrightness > 1) {
            newBrightness = 1;
        }
        mLayoutParams.screenBrightness = newBrightness;
        mWindow.setAttributes(mLayoutParams);
        scl.setProgress((int) (newBrightness * 100), 1);
        scl.setImageResource(R.drawable.brightness_w);
        scl.show();
    }

    private void controlVoice(float voiceDistance) {

        int value = control.getHeight() / maxVolume;
        int newVolume = (int) ((voiceDistance) / value + oldVolume);
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, newVolume, AudioManager.FLAG_PLAY_SOUND);

        //要强行转Float类型才能算出小数点，不然结果一直为0
        int volumeProgress = (int) (newVolume / Float.valueOf(maxVolume) * 100);
        if (volumeProgress >= 50) {
            scl.setImageResource(R.drawable.volume_higher_w);
        } else if (volumeProgress > 0) {
            scl.setImageResource(R.drawable.volume_lower_w);
        } else {
            scl.setImageResource(R.drawable.volume_off_w);
        }
        scl.setProgress(volumeProgress, 1);
        scl.show();
    }

    private void controlProgress(float progressDistance) {
        float offset = progressDistance;
        //根据移动的正负决定快进还是快退
        if (played == 1) {
            if (offset > 0) {
                scl.setImageResource(R.drawable.ff);

                newProgress = (int) (oldProgress + offset / control.getWidth() * seekBar.getMax());
                if (newProgress > seekBar.getMax()) {
                    newProgress = seekBar.getMax();
                }
            } else {
                scl.setImageResource(R.drawable.fr);
                newProgress = (int) (oldProgress + offset / control.getWidth() * seekBar.getMax());
                if (newProgress < 0) {
                    newProgress = 0;
                }
            }
            scl.setProgress(newProgress / seekBar.getMax() * 100, 0);
            scl.show();
            vView.seekTo(newProgress);
        }
    }

    private View.OnClickListener l = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.play_button:
                    if (vView.isPlaying()) {
                        //play.setImageResource(R.drawable.jz_play_normal);
                        vView.pause();
                        updatePausePlay();
                        played = 0;
                    } else {
                        if (firstPlay == 0) {
                            firstPlay = 1;
                            if (isShow) {
                                updateControl();
                                handler.removeCallbacks(runnable);
                            } else {
                                //隐藏
                                updateControl();
                                handler.postDelayed(runnable, 3000);//每三秒执行一次runnable.
                            }
                        }
                        // play.setImageResource(R.drawable.jz_pause_normal);
                        played = 1;
                        updateCurrrent();
                        vView.start();
                        updatePausePlay();
                        // hideLayout();
                        //  isShow = 1;
                    }
                    break;
                case R.id.app_video_play:
                    if (vView.isPlaying()) {
                        // play.setImageResource(R.drawable.jz_play_normal);
                        vView.pause();
                        updatePausePlay();
                        played = 0;
                    } else {
                        if (firstPlay == 0) {
                            firstPlay = 1;
                        }
                        // play.setImageResource(R.drawable.jz_pause_normal);
                        played = 1;
                        updateCurrrent();
                        vView.start();
                        updatePausePlay();
                        // hideLayout();
                        //  isShow = 1;
                    }
                    break;
                case R.id.dan:
                    clicked = 1;
                    commentDialog = new CommentDialog(Main2Activity.this);
                    commentDialog.show();
                    commentDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
                        @Override
                        public boolean onKey(DialogInterface dialogInterface, int keyCode, KeyEvent keyEvent) {
                            if (keyCode == KeyEvent.KEYCODE_BACK && keyEvent.getRepeatCount() == 0)
                                commentDialog.cancel();
                            return false;
                        }
                    });
                    break;
                case R.id.videoPlayer:

                    break;
                case R.id.full:
                    clicked = 1;
                    selfChange = 1;
                    fullScreen(0);
                    break;
                case R.id.control_layout:
                    break;
                case R.id.back:
                    if (stretch_flag == false) {
                        selfChange = 1;
                        fullScreen(0);
                    } else {
                        finish();
                    }
                    break;
               /* case R.id.ad_video_play :
                    vView.setVideoPath("http://www.geidco.org/html/files/2017-09/24/20170924163609090756674.mp4");
                    videoKind = 2 ;
                    initOption();
                    break;
                case R.id.start_flow :
                    break;*/
                case R.id.start_flow2:
                    if (!FloatWindowPermissionChecker.checkFloatWindowPermission()) {
                        FloatWindowPermissionChecker.askForFloatWindowPermission(Main2Activity.this);
                        return;
                    } else {
                        currentP = vView.getCurrentPosition();
                        FloatWindowManager.getInstance().showFloatWindow(Main2Activity.this);
                        finish();
                    }
                    break;
                case R.id.switch_clarity_button:
                    //changeClarityLayout.setVisibility(changeClarityLayout.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
                    if (clarityShow) {
                        changeClarityLayout.setVisibility(View.GONE);
                        clarityShow = false;
                    } else {
                        changeClarityLayout.setVisibility(View.VISIBLE);
                        clarityShow = true;
                    }
                    break;
                /*case R.id.low_clarity:
                    changeClar(lowClarity, 0);
                    break;
                case R.id.middle_clarity:
                    changeClar(midClarity, 1);
                    break;
                case R.id.high_clarity:
                    changeClar(highClarity, 2);
                    break;*/

                case R.id.replay_button:
                    vView.start();
                    replayLayout.setVisibility(View.GONE);
                    play.setImageResource(R.drawable.btn_content_pause_62);
                    controllPLay.setImageResource(R.drawable.icon_pause);
                    break;
                case R.id.warn_play_button:
                    if (isMobile) {
                        unregisterNetReceiver();
                        vView.start();
                        updatePausePlay();
                        netWarnLayout.setVisibility(View.GONE);
                    }
                    break;
                case R.id.lock_touch:
                    updateLock();
                    break;

            }
        }
    };

    private void updateLock() {
        if (isLocked) {
            isLocked = false;
            screenLock.setImageResource(R.drawable.icon_lock_open);
            //updateControl();
        } else {
            isLocked = true;
            screenLock.setImageResource(R.drawable.icon_lock);
            updateControl();
        }
    }

    private void setRePlay() {
        replayLayout.setVisibility(View.VISIBLE);
        replayTxt.setText("");

    }

    private void setNetWarn(String msg, String btnMsg) {
        netWarnLayout.setVisibility(View.VISIBLE);
        warnTxt.setText(msg);
        warnPlay.setText(btnMsg);
        updateControl();
    }

    //切换不同清晰度的视频
    private void changeClar(TextView which, int i) {
        if (i == 0) {
            Drawable drawable = getResources().getDrawable(R.drawable.background_textview);
            which.setBackground(drawable);
            midClarity.setBackground(null);
            highClarity.setBackground(null);
            changeClarityLayout.setVisibility(View.GONE);
            clarityShow = false;
        } else if (i == 1) {
            Drawable drawable = getResources().getDrawable(R.drawable.background_textview);
            which.setBackground(drawable);

            lowClarity.setBackground(null);
            highClarity.setBackground(null);
            changeClarityLayout.setVisibility(View.GONE);
            clarityShow = false;
        } else if (i == 2) {
            Drawable drawable = getResources().getDrawable(R.drawable.background_textview);
            which.setBackground(drawable);

            midClarity.setBackground(null);
            lowClarity.setBackground(null);
            changeClarityLayout.setVisibility(View.GONE);
            clarityShow = false;
        }

    }

    Handler handler = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            // TODO Auto-generated method stub
            if (clicked == 0) {
                //要做的事情
                updateControl();
                return;
            } else {
                clicked = 0;
            }
            handler.postDelayed(runnable, 3000);//每san秒执行一次runnable.
        }
    };


    private void updateControl() {

        if (isShow) {//隐藏
            isShow = false;
            //是显示状态，隐藏掉
            if (!isLocked) {
                control.setBackgroundColor(Color.parseColor("#00000000"));
                full.setVisibility(View.GONE);
                full.setAnimation(AnimationUtil.moveToViewBottom());
                danLayout.setVisibility(View.GONE);
                danLayout.setAnimation(AnimationUtil.moveToViewRight());
                backLayout.setVisibility(View.GONE);
                backLayout.setAnimation(AnimationUtil.upMoveToViewLocation());
                landLayout.setVisibility(View.GONE);
                landLayout.setAnimation(AnimationUtil.moveToViewBottom());
                play.setVisibility(View.GONE);
                screenLock.setVisibility(View.GONE);
            } else {
                //  control.setBackgroundColor(Color.parseColor("#00000000"));
                full.setVisibility(View.GONE);
                // full.setAnimation(AnimationUtil.moveToViewBottom());
                danLayout.setVisibility(View.GONE);
                // danLayout.setAnimation(AnimationUtil.moveToViewRight());
                backLayout.setVisibility(View.GONE);
                //  backLayout.setAnimation(AnimationUtil.upMoveToViewLocation());
                landLayout.setVisibility(View.GONE);
                // landLayout.setAnimation(AnimationUtil.moveToViewBottom());
                play.setVisibility(View.GONE);
                screenLock.setVisibility(View.GONE);
            }

        } else {//显示
            isShow = true;

            if (!isLocked) {
                //  control.setVisibility(View.VISIBLE);
                full.setVisibility(View.VISIBLE);
                full.setAnimation(AnimationUtil.moveToViewLocation());
                if (stretch_flag == false) {
                    // danLayout.setVisibility(View.VISIBLE);
                    // danLayout.setAnimation(AnimationUtil.moveToViewLeft());
                    screenLock.setVisibility(View.VISIBLE);
                }
                backLayout.setVisibility(View.VISIBLE);
                backLayout.setAnimation(AnimationUtil.downMoveToViewLocation());
                if (firstPlay == 1) {
                    landLayout.setVisibility(View.VISIBLE);
                    landLayout.setAnimation(AnimationUtil.moveToViewLocation());
                }
                play.setVisibility(View.VISIBLE);
                if (!stretch_flag) {
                    screenLock.setVisibility(View.VISIBLE);
                }
            } else {
                screenLock.setVisibility(View.VISIBLE);
            }
        }
    }

    private boolean stretch_flag = true;
    private int selfChange;

    /**
     * @param REVERSE 根据角度进行竖屏切换，如果为固定全屏则只能横屏切换
     *                根据角度进行横屏切换
     */
    private void fullScreen(int REVERSE) {
        Log.e("orientation", "stretch::" + isShow);
        if (stretch_flag) {

            stretch_flag = false;
            //切换成横屏
            full.setImageResource(R.drawable.icon_narrow_player_right);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            content.setVisibility(View.GONE);
            if (isShow) {
                updateControl();
            }
            if (REVERSE == 0) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            } else {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
            }

            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) vView.getLayoutParams();

            params.width = screenHeight;
            params.height = screenWidth;
            vView.setLayoutParams(params);
            control.setLayoutParams(params);
            iv.setLayoutParams(params);
            if (selfChange == 1) {
                currentOrientation = 270;
                selfChange = 0;
            }
        } else {
            stretch_flag = true;
            full.setImageResource(R.drawable.icon_large_player_right);
            screenLock.setVisibility(View.GONE);
            //切换成竖屏
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN); //显示状态栏
            danLayout.setVisibility(View.GONE);

            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

            content.setVisibility(View.VISIBLE);
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) vView.getLayoutParams();
            params.width = screenWidth;
            params.height = DensityUtil.dip2px(Main2Activity.this, 200);
            vView.setLayoutParams(params);
            control.setLayoutParams(params);
            iv.setLayoutParams(params);

            if (selfChange == 1) {
                currentOrientation = 0;
                selfChange = 0;
            }
        }

    }

    public static int getRealHeight(Context context, int hw) {
        Log.e("main2activity", "getRealHeight");
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        int screenHeight = 0;
        int screenWidth = 0;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            DisplayMetrics dm = new DisplayMetrics();
            display.getRealMetrics(dm);
            screenHeight = dm.heightPixels;
            screenWidth = dm.widthPixels;

            //或者也可以使用getRealSize方法
//      Point size = new Point();
//      display.getRealSize(size);
//      screenHeight = size.y;
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            try {
                screenHeight = (Integer) Display.class.getMethod("getRawHeight").invoke(display);
            } catch (Exception e) {
                DisplayMetrics dm = new DisplayMetrics();
                display.getMetrics(dm);
                screenHeight = dm.heightPixels;
                screenWidth = dm.widthPixels;
            }
        }
        if (hw == 0) {
            return screenHeight;
        } else {
            return screenWidth;
        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            //返回键
            if (stretch_flag == false) {
                selfChange = 1;
                fullScreen(0);
                isLocked = false;
                screenLock.setVisibility(View.GONE);
            } else {
                finish();
            }
            return false;
        } else {
            return super.onKeyDown(keyCode, event);
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e("MAin2Activity", "onDestory");
        vView.stopPlayback();
        myOrientoinListener.disable();
        getContentResolver().unregisterContentObserver(contentObserver);
        currentP = 0;
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        if (task != null) {
            task.cancel();
            task = null;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (vView.isPlaying()) {
            vView.pause();
            stopStatus = 1;
        } else {
            stopStatus = 0;
        }
        unregisterNetReceiver();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.e("MAin2Activity", "onRestart::" + stopStatus);
        if (stopStatus == 1) {
            vView.start();
        }
    }

    public ContentObserver contentObserver = new ContentObserver(new Handler()) {
        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            int navigationBarIsMin = Settings.System.getInt(getContentResolver(),
                    "navigationbar_is_min", 0);
            if (navigationBarIsMin == 1) {//导航键隐藏了
                Log.e("sjdksjdks", "导航隐藏");
            } else {//导航键显示了
                Log.e("sjdksjdks", "导航显示");
            }
        }
    };


    private int currentOrientation;

    @Override
    public void onGlobalLayout() {

    }


    class MyOrientoinListener extends OrientationEventListener {

        public MyOrientoinListener(Context context) {
            super(context);
        }

        public MyOrientoinListener(Context context, int rate) {
            super(context, rate);
        }

        @Override
        public void onOrientationChanged(int orientation) {

            //  int screenOrientation = getResources().getConfiguration().orientation;
            int isRotate = 0;
            try {
                isRotate = Settings.System.getInt(getContentResolver(), Settings.System.ACCELEROMETER_ROTATION);
            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
            }
            if (isRotate == 1) {

                if (orientation == OrientationEventListener.ORIENTATION_UNKNOWN) {
                    return;  //手机平放时，检测不到有效的角度
                }
                //只检测是否有四个角度的改变
                if (orientation > 350 || orientation < 10) { //0度
                    if (currentOrientation != 0) {
                        currentOrientation = 0;
                        fullScreen(0);
                    }
                } else if (orientation > 80 && orientation < 100) { //90度
                    if (currentOrientation != 90) {
                        currentOrientation = 90;
                        fullScreen(1);
                    }
                } else if (orientation > 170 && orientation < 190) { //180度
                    if (currentOrientation != 180) {
                        currentOrientation = 180;
                        fullScreen(0);
                    }
                } else if (orientation > 260 && orientation < 280) { //270度
                    if (currentOrientation != 270) {
                        currentOrientation = 270;
                        fullScreen(0);
                    }
                } else {
                    // Log.d("full", "else");
                    return;
                }
            }

        }
    }

    /**
     * 注册网络监听器
     */
    private void registerNetReceiver() {
        if (netChangeReceiver == null) {
            IntentFilter filter = new IntentFilter(
                    ConnectivityManager.CONNECTIVITY_ACTION);
            netChangeReceiver = new NetChangeReceiver();
            registerReceiver(netChangeReceiver, filter);
        }
    }

    /**
     * 销毁网络监听器
     */
    private void unregisterNetReceiver() {
        if (netChangeReceiver != null) {
            unregisterReceiver(netChangeReceiver);
            netChangeReceiver = null;
        }
    }

    public class NetChangeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (NetUtils.getNetworkType(Main2Activity.this) == 3) {// 网络是WIFI

            } else if (NetUtils.getNetworkType(Main2Activity.this) == 2
                    || NetUtils.getNetworkType(Main2Activity.this) == 4) {// 网络不是手机网络或者是以太网
                // TODO 更新状态是暂停状态,是否是直播
                // statusChange(STATUS_PAUSE);
                isMobile = true;
                vView.pause();//暂停播放
                updatePausePlay(); //更新播放按钮的图标状态
                setNetWarn("播放将消耗48MB流量", "继续播放");
                // $.id(com.superplayer.library.R.id.app_video_loading).gone();//加载progressbar隐藏
               /* onNetChangeListener.onMobile();
                showStatus(
                        activity.getResources().getString(
                                com.superplayer.library.R.string.player_not_wifi), "继续");*/ //提示
            } else if (NetUtils.getNetworkType(Main2Activity.this) == 1) {// 网络链接断开
                //  onPause();
                // onNetChangeListener.onDisConnect();
                setNetWarn("网络错误", "重试");
                currentP = vView.getCurrentPosition();
                Log.e("network", "断开");
            } else {
                setNetWarn("网络错误", "重试");
                currentP = vView.getCurrentPosition();
                Log.e("network", "网络不可用");
                //onNetChangeListener.onNoAvailable();
            }
        }
    }

    private void updatePausePlay() {
        //将播放按钮设置成暂停状态
        if (vView.isPlaying()) {
            play.setImageResource(R.drawable.btn_content_pause_62);
            controllPLay.setImageResource(R.drawable.icon_pause);
        } else {
            play.setImageResource(R.drawable.btn_content_play_62);
            controllPLay.setImageResource(R.drawable.icon_play);
        }
    }


    /**
     * 发送评论回调
     */
    public interface liveCommentResult {
        void onResult(boolean confirmed, String comment);
    }

    public static Bitmap getVideoThumb(String path) {
        MediaMetadataRetriever media = new MediaMetadataRetriever();
        media.setDataSource(path);
        return media.getFrameAtTime();
    }


}
