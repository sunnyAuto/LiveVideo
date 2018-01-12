package com.sunxiao.mathapplication.View;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pili.pldroid.player.AVOptions;
import com.pili.pldroid.player.widget.PLVideoTextureView;
import com.sunxiao.mathapplication.Main2Activity;
import com.sunxiao.mathapplication.R;
import com.sunxiao.mathapplication.controller.DensityUtil;

/**
 * Created by NJ on 2018/1/10.
 */

public class MyVideoView extends PLVideoTextureView{
    private PLVideoTextureView videoTextureView ;
    private int codec ;
    private Context context ;
    private String videoPath ;
    private int videoKind ;
    private View mView ;
    private VideoGestureRelativeLayout controller ;
    private boolean stretch_flag=true;
    private int selfChange;

    public MyVideoView(Context context) {
        super(context);
        this.context = context ;

    }



    public MyVideoView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public MyVideoView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    public MyVideoView(Context context, AttributeSet attributeSet, int i, int i1) {
        super(context, attributeSet, i, i1);
    }

private void initView(Context context){
    mView  = LayoutInflater.from(context).inflate(R.layout.my_video_view,null);
    videoTextureView = (PLVideoTextureView) mView.findViewById(R.id.video_view);
    controller = (VideoGestureRelativeLayout) mView.findViewById(R.id.control_layout);
}
private View.OnClickListener l = new OnClickListener() {
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.control_layout :
                break;
        }
    }
};

    /**
     *
     * @param REVERSE
     * 根据角度进行竖屏切换，如果为固定全屏则只能横屏切换
     *根据角度进行横屏切换
     *
     */
    private void fullScreen(int REVERSE) {
    }


}
