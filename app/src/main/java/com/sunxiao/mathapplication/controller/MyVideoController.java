package com.sunxiao.mathapplication.controller;

import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pili.pldroid.player.IMediaController;
import com.sunxiao.mathapplication.R;

import static android.content.ContentValues.TAG;

/**
 * Created by NJ on 2018/1/11.
 */

public class MyVideoController  {
    /*private MediaPlayerControl mPlayer;
    private ImageButton mPauseButton;
    private Context mContext;
    private PopupWindow mWindow;
    private int mAnimStyle;
    private View mAnchor;
    private View mRoot;
    private long mDuration;
    private boolean mShowing;
    private boolean mDragging;
    private boolean mInstantSeeking = true;
    private static int sDefaultTimeout = 3000;
    private static final int SEEK_TO_POST_DELAY_MILLIS = 200;

    public MyVideoController(Context context) {
        super(context);
    }

    public MyVideoController(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyVideoController(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setMediaPlayer(MediaPlayerControl player) {
        mPlayer = player;
        updatePausePlay();
    }

    @Override
    public void show() {
        show(sDefaultTimeout);
    }

    @Override
    public void show(int timeout) {
        if (!mShowing) {
            if (mAnchor != null && mAnchor.getWindowToken() != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                    mAnchor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
                }
            }
            if (mPauseButton != null)
                mPauseButton.requestFocus();
            disableUnsupportedButtons();

            if (mFromXml) {
                setVisibility(View.VISIBLE);
            } else {
                int[] location = new int[2];

                if (mAnchor != null) {
                    mAnchor.getLocationOnScreen(location);
                    Rect anchorRect = new Rect(location[0], location[1],
                            location[0] + mAnchor.getWidth(), location[1]
                            + mAnchor.getHeight());

                    mWindow.setAnimationStyle(mAnimStyle);
                    mWindow.showAtLocation(mAnchor, Gravity.BOTTOM,
                            anchorRect.left, 0);
                } else {
                    Rect anchorRect = new Rect(location[0], location[1],
                            location[0] + mRoot.getWidth(), location[1]
                            + mRoot.getHeight());

                    mWindow.setAnimationStyle(mAnimStyle);
                    mWindow.showAtLocation(mRoot, Gravity.BOTTOM,
                            anchorRect.left, 0);
                }
            }
            mShowing = true;
            if (mShownListener != null)
                mShownListener.onShown();
        }
        updatePausePlay();
        mHandler.sendEmptyMessage(SHOW_PROGRESS);

        if (timeout != 0) {
            mHandler.removeMessages(FADE_OUT);
            mHandler.sendMessageDelayed(mHandler.obtainMessage(FADE_OUT),
                    timeout);
        }
    }

    @Override
    public void hide() {
        if (mShowing) {
            if (mAnchor != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                    //mAnchor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
                }
            }
            try {
                mHandler.removeMessages(SHOW_PROGRESS);
                if (mFromXml)
                    setVisibility(View.GONE);
                else
                    mWindow.dismiss();
            } catch (IllegalArgumentException ex) {
                Log.d(TAG, "MediaController already removed");
            }
            mShowing = false;
            if (mHiddenListener != null)
                mHiddenListener.onHidden();
        }
    }

    @Override
    public boolean isShowing() {
        return mShowing;
    }

    @Override
    public void setAnchorView(View view) {
        mAnchor = view;
        if (mAnchor == null) {
            sDefaultTimeout = 0; // show forever
        }
        if (!mFromXml) {
            removeAllViews();
            mRoot = LayoutInflater.from(mContext).inflate(R.layout.controller_layout,this);
            mWindow.setContentView(mRoot);
            mWindow.setWidth(FrameLayout.LayoutParams.MATCH_PARENT);
            mWindow.setHeight(FrameLayout.LayoutParams.WRAP_CONTENT);
        }
        initControllerView(mRoot);
    }
    private void updatePausePlay() {
        if (mRoot == null || mPauseButton == null)
            return;

        if (mPlayer.isPlaying())
            //暂停
            mPauseButton.setImageResource(IC_MEDIA_PAUSE_ID);
        else
            //播放
            mPauseButton.setImageResource(IC_MEDIA_PLAY_ID);
    }

    private void makeControllerView(){

    }*/

}
