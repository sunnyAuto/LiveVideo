package com.sunxiao.mathapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;

public class Main5Activity extends AppCompatActivity {
    private WebView wb ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main5);
        wb = (WebView) findViewById(R.id.wv);

        wb.loadUrl("http://mlive.nengapp.com/watch/1526876");
    }
}
