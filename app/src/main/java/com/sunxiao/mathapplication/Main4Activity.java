package com.sunxiao.mathapplication;

import android.content.res.Configuration;
import android.database.ContentObserver;
import android.os.Environment;
import android.os.Handler;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.sunxiao.mathapplication.manager.FloatManager2;
import com.sunxiao.mathapplication.manager.FloatWindowManager;
import com.sunxiao.mathapplication.manager.FloatWindowPermissionChecker;
import com.superplayer.library.SuperPlayer;
import com.superplayer.library.utils.ShareInterface;

import java.util.ArrayList;
import java.util.List;

public class Main4Activity extends AppCompatActivity implements SuperPlayer.OnNetChangeListener {
    private SuperPlayer player;
    private boolean isLive;
    /**
     * 测试地址
     */
    private String videoUrl;
    private List<com.superplayer.library.ClarityBean> list = new ArrayList<>();
    private int currentPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main4);
        getContentResolver().registerContentObserver(Settings.System.getUriFor("navigationbar_is_min"), true, contentObserver);
        initIntent();
        initView();
    }
    //监听底部导航栏的展开与收起
    public ContentObserver contentObserver = new ContentObserver(new Handler()) {
        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            int navigationBarIsMin = Settings.System.getInt(getContentResolver(),
                    "navigationbar_is_min", 0);
            if (navigationBarIsMin == 1) {//导航键隐藏了
                Log.e("sjdksjdks","导航隐藏");
            } else {//导航键显示了
                Log.e("sjdksjdks","导航显示");
            }
        }
    };

    private void initIntent() {
        // url = getIntent().getStringExtra("url");
        // isLive = getIntent().getBooleanExtra("isLive",false);

        if (getIntent().getStringExtra("path") != null) {

            videoUrl = getIntent().getStringExtra("path");
            currentPosition = Integer.parseInt(getIntent().getStringExtra("currentPosition"));
        }else {
            list = (List<com.superplayer.library.ClarityBean>) getIntent().getSerializableExtra("url");
            Log.e("videoUrl", "url::" + list);
        }
    }

    private void initView() {

        player = (SuperPlayer) findViewById(R.id.player_layout);
        player.showCenterControl(false);
        player.setSupportGesture(true);

        if (list.size() > 0) {
            player.setDiffentClarity(list);
            videoUrl = list.get(2).getVideoPath();

            if (list.get(2).getIsLive() == 1) {
                isLive = true;
            } else {
                isLive = false;
            }
            if (isLive) {
                player.setLive(true);//设置该地址是直播的地址
            }
        }


        player.setNetChangeListener(true)//设置监听手机网络的变化
                .setOnNetChangeListener(this)//实现网络变化的回调
                .onPrepared(new SuperPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared() {
                        /**
                         * 监听视频是否已经准备完成开始播放。（可以在这里处理视频封面的显示跟隐藏）
                         */
                        Log.e("player", "onprepare");
                        // player.play(url,5000);//开始播放视频
                        //    player.playSwitch(Environment.getExternalStorageDirectory().getPath() + "/DCIM/Camera/VID_20180109_224623.mp4");

                    }
                }).onComplete(new Runnable() {
            @Override
            public void run() {
                /**
                 * 监听视频是否已经播放完成了。（可以在这里处理视频播放完成进行的操作）
                 */
            }
        }).onInfo(new SuperPlayer.OnInfoListener() {
            @Override
            public void onInfo(int what, int extra) {
                /**
                 * 监听视频的相关信息。
                 */

            }
        }).onError(new SuperPlayer.OnErrorListener() {
            @Override
            public void onError(int what, int extra) {
                /**
                 * 监听视频播放失败的回调
                 */

            }
        }).setTitle(videoUrl)//设置视频的titleName
                .play(videoUrl, currentPosition);//开始播放视频

        player.setScaleType(SuperPlayer.SCALETYPE_FITXY);

        player.startFlowWindow(new ShareInterface() {
            @Override
            public void onClick() {
                if (!FloatWindowPermissionChecker.checkFloatWindowPermission()) {
                    FloatWindowPermissionChecker.askForFloatWindowPermission(Main4Activity.this);
                    return;
                } else {
                    currentPosition = player.getCurrentPosition();
                    FloatManager2.getInstance().showFloatWindow(Main4Activity.this, videoUrl, currentPosition);
                    finish();
                }
            }

            @Override
            public void getURl(String url) {
                videoUrl = url;
            }
        });

    }

    @Override
    public void onWifi() {

    }

    @Override
    public void onMobile() {

    }

    @Override
    public void onDisConnect() {

    }

    @Override
    public void onNoAvailable() {

    }


    /**
     * 下面的这几个Activity的生命状态很重要
     */
    @Override
    protected void onPause() {
        super.onPause();
        if (player != null) {
            player.onPause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (player != null) {
            player.onResume();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getContentResolver().unregisterContentObserver(contentObserver);
        if (player != null) {
            player.onDestroy();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (player != null) {
            player.onConfigurationChanged(newConfig);
        }
    }

    @Override
    public void onBackPressed() {
        if (player != null && player.onBackPressed()) {
            return;
        }
        super.onBackPressed();
    }

}
