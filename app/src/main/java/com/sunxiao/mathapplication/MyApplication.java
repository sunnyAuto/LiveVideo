package com.sunxiao.mathapplication;

import android.app.Application;
import android.content.Context;

/**
 * Created by NJ on 2018/1/9.
 */

public class MyApplication extends Application {
    private static MyApplication instance;
    public static MyApplication getInstance(){
        return instance;
    }
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        instance = this ;
    }
}
