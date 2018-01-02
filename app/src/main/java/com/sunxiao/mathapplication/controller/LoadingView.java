package com.sunxiao.mathapplication.controller;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sunxiao.mathapplication.R;

/**
 * Created by NJ on 2017/12/14.
 */

public class LoadingView extends RelativeLayout {
    private Context mContext;
    private ImageView loadingIv;
    private TextView loadingTv;

    public LoadingView(Context context) {
        super(context);
        this.mContext = context;
        initView();
    }

    public LoadingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        initView();
    }

    private void initView() {
        View view = LayoutInflater.from(mContext).inflate(R.layout.loading, null);

        loadingIv = (ImageView) view.findViewById(R.id.loadingIv);
        loadingTv = (TextView) view.findViewById(R.id.loadingTv);

        AnimationDrawable animationDrawable = (AnimationDrawable) loadingIv.getBackground();
        if (animationDrawable != null)
            animationDrawable.start();

        addView(view);
    }

    public ImageView getLoadingIv() {
        return loadingIv;
    }

    public TextView getLoadingTv() {
        return loadingTv;
    }
}
