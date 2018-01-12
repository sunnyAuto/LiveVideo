package com.sunxiao.mathapplication;


import android.Manifest;
import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.MediaController;


import com.pili.pldroid.player.IMediaController;
import com.pili.pldroid.player.PLMediaPlayer;
import com.pili.pldroid.player.widget.PLVideoView;

public class MainActivity extends MPermissionsActivity {
    private PLVideoView plMediaPlayer ;
    private Button btn ,btn1 ,btn2 , adBtn;
    private EditText input ;
    private int ki ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initPersion();
    }

    private void initPersion() {
        requestPermission(new String[]{ Manifest.permission.WRITE_EXTERNAL_STORAGE ,
                Manifest.permission.READ_EXTERNAL_STORAGE}, 0x0001);
    }
    private void initView() {
        plMediaPlayer= (PLVideoView) findViewById(R.id.PLVideoView);
        btn = (Button) findViewById(R.id.btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this ,Main2Activity.class);
                String path  = Environment.getExternalStorageDirectory().getPath() + "/DCIM/Camera/VID_20180109_224623.mp4";
                intent.putExtra("path",path);
                intent.putExtra("kind",2);
            //    intent.putExtra("currentPosition","0");
                startActivity(intent);
            }
        });
        btn1 = (Button) findViewById(R.id.btn1);
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this ,Main2Activity.class);
                String path = "http://live.hkstv.hk.lxdns.com/live/hks/playlist.m3u8";
                intent.putExtra("path",path);
                if (TextUtils.isEmpty(input.getText())){
                    ki = 1;
                }else {
                    ki = Integer.parseInt(input.getText().toString());
                }
                intent.putExtra("kind",ki);
               // intent.putExtra("currentPosition","0");
                startActivity(intent);
            }
        });
        btn2 = (Button) findViewById(R.id.btn2);
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this ,Main2Activity.class);
                String path = "http://oyoufznq9.bkt.clouddn.com/1neng";
                intent.putExtra("path",path);
                intent.putExtra("kind",2);
              //  intent.putExtra("currentPosition","0");
                startActivity(intent);
            }
        });
        input  = (EditText) findViewById(R.id.input);
        adBtn = (Button) findViewById(R.id.to_ad);
        adBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this ,AdActivity.class);
                startActivity(intent);
            }
        });

    }


}
