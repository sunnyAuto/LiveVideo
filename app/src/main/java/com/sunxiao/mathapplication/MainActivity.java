package com.sunxiao.mathapplication;


import android.Manifest;
import android.content.Intent;
import android.os.Environment;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.MediaController;


import com.pili.pldroid.player.IMediaController;
import com.pili.pldroid.player.PLMediaPlayer;
import com.pili.pldroid.player.widget.PLVideoView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends MPermissionsActivity {
    private PLVideoView plMediaPlayer;
    private Button btn, btn1, btn2, adBtn, main4Btn;
    private EditText input;
    private int ki;
    private List<com.superplayer.library.ClarityBean> list = new ArrayList<>();
    private List<ClarityBean> clarityList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initPersion();
        initUrlColection();
        initUrlClarity();
    }

    private void initUrlColection() {
        com.superplayer.library.ClarityBean c = new com.superplayer.library.ClarityBean();
        c.setVideoPath(Environment.getExternalStorageDirectory().getPath() + "/DCIM/Camera/VID_20180109_224623.mp4");
        c.setClarityKinds("标清(480P)");
        c.setIsLive(0);
        list.add(c);

        com.superplayer.library.ClarityBean c1 = new com.superplayer.library.ClarityBean();
        c1.setVideoPath("http://baobab.wandoujia.com/api/v1/playUrl?vid=2614&editionType=normal");
        c1.setClarityKinds("标清(560P)");
        c1.setIsLive(1);
        list.add(c1);

        com.superplayer.library.ClarityBean c2 = new com.superplayer.library.ClarityBean();
        c2.setVideoPath("http://oyoufznq9.bkt.clouddn.com/1neng");
        c2.setClarityKinds("标清(720P)");
        c2.setIsLive(0);
        list.add(c2);
        Log.e("colocetion","list:::"+list);
    }
    private void initUrlClarity() {
        ClarityBean c = new ClarityBean();
        c.setVideoPath(Environment.getExternalStorageDirectory().getPath() + "/DCIM/Camera/VID_20180109_224623.mp4");
        c.setClarityKinds("普清");
        c.setDefaultClarity(0);
        c.setIsLive(0);
        clarityList.add(c);

        ClarityBean c1 = new ClarityBean();
        c1.setVideoPath("http://baobab.wandoujia.com/api/v1/playUrl?vid=2614&editionType=normal");
        c1.setClarityKinds("标清");
        c1.setIsLive(1);
        c1.setDefaultClarity(0);
        clarityList.add(c1);

        ClarityBean c2 = new ClarityBean();
        c2.setVideoPath("http://oyoufznq9.bkt.clouddn.com/1neng");
        c2.setClarityKinds("高清");
        c2.setIsLive(0);
        c1.setDefaultClarity(1);
        clarityList.add(c2);
        Log.e("colocetion","list:::"+list);
    }

    private void initPersion() {
        requestPermission(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE}, 0x0001);
    }

    private void initView() {
        plMediaPlayer = (PLVideoView) findViewById(R.id.PLVideoView);
        btn = (Button) findViewById(R.id.btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Main2Activity.class);
                /*String path = Environment.getExternalStorageDirectory().getPath() + "/DCIM/Camera/VID_20180109_224623.mp4";
                intent.putExtra("path", path);
                intent.putExtra("kind", 2);*/
                intent.putExtra("url", (Serializable) clarityList);
                //    intent.putExtra("currentPosition","0");
                startActivity(intent);
            }
        });
        btn1 = (Button) findViewById(R.id.btn1);
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Main2Activity.class);
                String path = "http://live.hkstv.hk.lxdns.com/live/hks/playlist.m3u8";
                intent.putExtra("path", path);
                if (TextUtils.isEmpty(input.getText())) {
                    ki = 1;
                } else {
                    ki = Integer.parseInt(input.getText().toString());
                }
                intent.putExtra("kind", ki);

                // intent.putExtra("currentPosition","0");
                startActivity(intent);
            }
        });
        btn2 = (Button) findViewById(R.id.btn2);
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Main2Activity.class);
                String path = "http://oyoufznq9.bkt.clouddn.com/1neng";
                intent.putExtra("path", path);
                intent.putExtra("kind", 2);
                //  intent.putExtra("currentPosition","0");
                startActivity(intent);
            }
        });
        input = (EditText) findViewById(R.id.input);
        adBtn = (Button) findViewById(R.id.to_ad);
        adBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AdActivity.class);
                startActivity(intent);
            }
        });

        main4Btn = (Button) findViewById(R.id.to_main4);
        main4Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Main4Activity.class);
                // intent.putExtra("url",Environment.getExternalStorageDirectory().getPath() + "/DCIM/Camera/VID_20180109_224623.mp4");
                /*intent.putExtra("url", "http://live.hkstv.hk.lxdns.com/live/hks/playlist.m3u8");
                intent.putExtra("isLive",true);*/
                intent.putExtra("url", (Serializable) list);

                startActivity(intent);
            }
        });
    }


}
