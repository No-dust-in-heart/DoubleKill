package com.example.lei.doublekill.ui;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.example.lei.doublekill.MainActivity;
import com.example.lei.doublekill.R;
import com.example.lei.doublekill.utils.ShareUtils;
import com.example.lei.doublekill.utils.StaticClass;
import com.example.lei.doublekill.utils.UtilTools;

public class SplashActivity extends AppCompatActivity {
    /**
     * 1.延时2000ms
     * 2.判断程序是否是第一次运行
     * 3.自定义字体
     * 4.Activity全屏主题
     */
    private TextView tv_splash;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case StaticClass.HANDLE_SPLASH:
                    //判断是否是第一次运行
                    if (isFirst()) {
                        startActivity(new Intent(SplashActivity.this, GuideActivity.class));
                    } else {
                        startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                    }
                    finish();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        initView();
    }

    private void initView() {
        //延时2000ms发送消息
        handler.sendEmptyMessageDelayed(StaticClass.HANDLE_SPLASH, 2000);
        tv_splash = findViewById(R.id.tv_splash);
        //设置字体
        UtilTools.setFont(this, tv_splash);
    }

    private boolean isFirst() {
        //看看是否有键为StaticClass.SHARE_IS_FIRST的值存在,若为空则说明是第一次启动
        boolean isFirst = ShareUtils.getBoolean(this, StaticClass.SHARE_IS_FIRST, true);
        if (isFirst) {//是第一次运行
            //存入值表示破冰完成
            ShareUtils.putBoolean(this, StaticClass.SHARE_IS_FIRST, false);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
    }
}
