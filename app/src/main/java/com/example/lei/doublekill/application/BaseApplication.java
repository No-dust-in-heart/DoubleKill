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
//需要在Manifest文件的<application>中增加name属性替换默认的Application，用于做统一的初始化
//它是整个项目中生命周期最长的，贯穿整个项目周期
public class BaseApplication extends Application{
    @Override
    public void onCreate() {
        super.onCreate();
        //初始化Bugly
        /**
         * 第三个参数为SDK调试模式开关，调试模式的行为特性如下：
                输出详细的Bugly SDK的Log；
                每一条Crash都会被立即上报；
                自定义日志将会在Logcat中输出。
                建议在测试阶段建议设置成true，发布时设置为false。
         */
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
