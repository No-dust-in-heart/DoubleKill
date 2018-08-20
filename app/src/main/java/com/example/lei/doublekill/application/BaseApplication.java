package com.example.lei.doublekill.application;

import android.app.Application;
import android.content.Intent;

import com.example.lei.doublekill.service.SmsService;
import com.example.lei.doublekill.utils.LogUtil;
import com.example.lei.doublekill.utils.ShareUtils;
import com.example.lei.doublekill.utils.StaticClass;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechUtility;
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
        //初始化科大讯飞的语音合成功能
        // 将“12345678”替换成您申请的APPID，申请地址：http://www.xfyun.cn
        // 请勿在“=”与appid之间添加任何空字符或者转义符
        SpeechUtility.createUtility(getApplicationContext(),
                SpeechConstant.APPID +"="+StaticClass.VOICE_KEY);

        //启动App的时候，初始化读取短信功能的状态。
        boolean isSms= ShareUtils.getBoolean(this,"isSms",false);
        if(isSms){
            startService(new Intent(this,SmsService.class));
        }
    }
}
