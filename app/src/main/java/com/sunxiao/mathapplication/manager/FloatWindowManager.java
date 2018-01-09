package com.sunxiao.mathapplication.manager;

import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.PixelFormat;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.pili.pldroid.player.AVOptions;
import com.pili.pldroid.player.PLMediaPlayer;
import com.pili.pldroid.player.widget.PLVideoTextureView;
import com.sunxiao.mathapplication.Main2Activity;
import com.sunxiao.mathapplication.MyApplication;
import com.sunxiao.mathapplication.R;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static android.view.WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED;
import static android.view.WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;
import static android.view.WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
import static android.view.WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
import static android.view.WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
import static android.view.WindowManager.LayoutParams.TYPE_TOAST;

/**
 * Created by feifan on 2017/8/21.
 * Contacts me:404619986@qq.com
 */

public class FloatWindowManager {
    @SuppressLint("StaticFieldLeak")
    private static FloatWindowManager sInstance = new FloatWindowManager();
    private WindowManager mWindowManager;
    private Context mContext;

    //小窗布局相关
    private View mSmallWindow;
    private WindowManager.LayoutParams mSmallWindowParams;
    private DisplayMetrics mDisplayMetrics;
    private int mVideoViewWidth;
    private int mVideoViewHeight;
    private int mStatusBarHeight;
    private int mNavigationBarHeight;
    private int dp12;
    private boolean isAddToWindow = false;
    private int codec ;
    private  PLVideoTextureView fakeVideoView ;
    private int videoKind ;
    private String videoPath ;

    private FloatWindowManager() {
        mContext = MyApplication.getInstance();
        mWindowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        mStatusBarHeight = SystemBarUtils.getStatusBarHeight(mContext);
        mNavigationBarHeight = SystemBarUtils.getNavigationBarHeight(mContext);
        createSmallWindow();
    }

    public static FloatWindowManager getInstance() {
        return sInstance;
    }

    public void showFloatWindow(Context activity) {
        if (!FloatWindowPermissionChecker.checkFloatWindowPermission()) {
            FloatWindowPermissionChecker.askForFloatWindowPermission(activity);
            return;
        }
        if (isAddToWindow) return;
        try {
            mWindowManager.addView(mSmallWindow, mSmallWindowParams);
        } catch (Exception e) {
            mWindowManager.updateViewLayout(mSmallWindow, mSmallWindowParams);
        }
        isAddToWindow = true;
    }

    public void removeFromWindow() {
        if (!isAddToWindow) return;
        mWindowManager.removeView(mSmallWindow);
        isAddToWindow = false;
    }
    private static float lastX;
    private static float lastY;
    private void createSmallWindow() {
        mSmallWindow = LayoutInflater.from(mContext).inflate(R.layout.layout_float_window, null);
        fakeVideoView = (PLVideoTextureView) mSmallWindow.findViewById(R.id.video);
        initOption();
        videoListener() ;
        fakeVideoView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN :
                        lastX = event.getRawX();
                        lastY = event.getRawY();

                        return true;
                    case MotionEvent.ACTION_MOVE :
                        float moveX = event.getRawX() - lastX;
                        float moveY = event.getRawY() - lastY;
                        lastX = event.getRawX();
                        lastY = event.getRawY();


                        mSmallWindowParams.x += moveX;
                        mSmallWindowParams.y += moveY;
                        if (isAddToWindow) {
                            mWindowManager.updateViewLayout(mSmallWindow, mSmallWindowParams);
                        }
                        return  true ;

                    case MotionEvent.ACTION_UP :
                        final int currentX = mSmallWindowParams.x;
                        final int currentY = mSmallWindowParams.y;
                        int dx = 0;
                        int dy = 0;
                        if (mSmallWindowParams.x > mDisplayMetrics.widthPixels - mVideoViewWidth) {
                            dx = (mDisplayMetrics.widthPixels - mVideoViewWidth) - mSmallWindowParams.x;
                        } else if (mSmallWindowParams.x < 0) {
                            dx = -mSmallWindowParams.x;
                        }
                        if (mSmallWindowParams.y > mDisplayMetrics.heightPixels - mVideoViewHeight - mStatusBarHeight) {
                            dy = (mDisplayMetrics.heightPixels - mVideoViewHeight - mStatusBarHeight) - mSmallWindowParams.y;
                        } else if (mSmallWindowParams.y < 0) {
                            dy = -mSmallWindowParams.y;
                        }
                        if (dx == 0 && dy == 0) {
                           // return;
                            Log.e("dxdy","dx::"+dx+":::dy::"+dy);
                        }
                        ValueAnimator x = ValueAnimator.ofInt(0, dx).setDuration(300);
                        x.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                            @Override
                            public void onAnimationUpdate(ValueAnimator animation) {
                                mSmallWindowParams.x = currentX + (int) animation.getAnimatedValue();
                            }
                        });
                        ValueAnimator y = ValueAnimator.ofInt(0, dy).setDuration(300);
                        y.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                            @Override
                            public void onAnimationUpdate(ValueAnimator animation) {
                                mSmallWindowParams.y = currentY + (int) animation.getAnimatedValue();
                                if (isAddToWindow) {
                                    mWindowManager.updateViewLayout(mSmallWindow, mSmallWindowParams);
                                }
                            }
                        });
                        AnimatorSet animatorSet = new AnimatorSet();
                        animatorSet.play(x).with(y);
                        animatorSet.start();

                }
                return false;
            }
        });
        mSmallWindow.findViewById(R.id.close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fakeVideoView.stopPlayback();
                removeFromWindow();
            }
        });

        ViewGroup.LayoutParams videoLayoutParams = fakeVideoView.getLayoutParams();
        mDisplayMetrics = Resources.getSystem().getDisplayMetrics();
        mVideoViewWidth = (int) (mDisplayMetrics.widthPixels * 0.65);
        mVideoViewHeight = (int) (mVideoViewWidth / 16.0 * 9.0) + 1;
        videoLayoutParams.width = mVideoViewWidth;
        videoLayoutParams.height = mVideoViewHeight;
        fakeVideoView.setLayoutParams(videoLayoutParams);

        dp12 = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 12, mDisplayMetrics);
        mSmallWindowParams = new WindowManager.LayoutParams();
        mSmallWindowParams.width = WRAP_CONTENT;
        mSmallWindowParams.height = WRAP_CONTENT;
        mSmallWindowParams.gravity = Gravity.TOP | Gravity.START;
        mSmallWindowParams.x = Resources.getSystem().getDisplayMetrics().widthPixels - mVideoViewWidth - dp12;
        mSmallWindowParams.y = Resources.getSystem().getDisplayMetrics().heightPixels - mVideoViewHeight - dp12 - mStatusBarHeight - mNavigationBarHeight;

        if (Build.VERSION.SDK_INT >= 19 && Build.VERSION.SDK_INT < 23) {
            mSmallWindowParams.type = TYPE_TOAST;
        } else {
            mSmallWindowParams.type = TYPE_SYSTEM_ALERT;
        }
        mSmallWindowParams.flags = FLAG_NOT_FOCUSABLE | FLAG_NOT_TOUCH_MODAL | FLAG_HARDWARE_ACCELERATED | FLAG_LAYOUT_NO_LIMITS;
        mSmallWindowParams.format = PixelFormat.RGBA_8888;
        mSmallWindowParams.windowAnimations = android.R.style.Animation_Translucent;
    }
    private void initOption() {
        videoKind = Main2Activity.videoKind ;
        videoPath = Main2Activity.videoPath ;
        codec = Main2Activity.codec ;

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
        //options.setInteger(AVOptions.KEY_PREFER_FORMAT, 2);

        options.setInteger(AVOptions.KEY_PREFER_FORMAT, videoKind);
        // 请在开始播放之前配置
        fakeVideoView.setAVOptions(options);
        fakeVideoView.setVideoPath(videoPath);
    }
    private void videoListener(){
        fakeVideoView.setOnPreparedListener(new PLMediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(PLMediaPlayer plMediaPlayer, int i) {
                Log.e("current","current::"+Main2Activity.currentP);
                fakeVideoView.start();
                fakeVideoView.post(new Runnable() {
                    @Override
                    public void run() {
                        fakeVideoView.seekTo(Main2Activity.currentP);
                    }
                });

              //
            }
        });
    }
}
