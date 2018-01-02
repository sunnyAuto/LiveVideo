package com.sunxiao.mathapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import cn.jzvd.JZVideoPlayer;
import cn.jzvd.JZVideoPlayerStandard;

public class BiliPlayerActivity extends AppCompatActivity {
    private JZVideoPlayerStandard jzVideoPlayer ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bili_player);
        initView();
    }

    private void initView() {
        jzVideoPlayer = (JZVideoPlayerStandard) findViewById(R.id.videoplayer);
        jzVideoPlayer.setUp("http://jzvd.nathen.cn/c6e3dc12a1154626b3476d9bf3bd7266/6b56c5f0dc31428083757a45764763b0-5287d2089db37e62345123a1be272f8b.mp4"
                , jzVideoPlayer.SCREEN_WINDOW_NORMAL, "饺子闭眼睛");
       // jzVideoPlayer.thumbImageView.setImageURI("http://p.qpic.cn/videoyun/0/2449_43b6f696980311e59ed467f22794e792_1/640");
    }
}
