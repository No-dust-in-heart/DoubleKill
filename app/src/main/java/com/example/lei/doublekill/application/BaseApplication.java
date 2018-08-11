package com.example.lei.doublekill.application;

import android.app.Application;

import com.example.lei.doublekill.utils.LogUtil;
import com.example.lei.doublekill.utils.StaticClass;
import com.tencent.bugly.crashreport.CrashReport;

import cn.bmob.v3.Bmob;
//需要在Manifest文件的<application>中配置name属性
public class BaseApplication extends Application{
    @Override
    public void onCreate() {
        super.onCreate();
        //初始化Bugly
        CrashReport.initCrashReport(getApplicationContext(), StaticClass.BUGLY_APP_ID, true);
        //初始化Bmob
        Bmob.initialize(this, StaticClass.BMOB_APP_ID);
        LogUtil.d("BaseApplication");
    }
}
