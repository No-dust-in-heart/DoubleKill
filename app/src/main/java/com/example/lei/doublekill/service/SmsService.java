package com.example.lei.doublekill.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.IBinder;
import android.telephony.SmsMessage;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.example.lei.doublekill.R;
import com.example.lei.doublekill.utils.LogUtil;
import com.example.lei.doublekill.utils.StaticClass;
import com.example.lei.doublekill.view.DispatchLinearLayout;

public class SmsService extends Service implements View.OnClickListener {

    private SmsReceiver smsReceiver;
    //发件人号码
    private String smsPhone;
    //短信内容
    private String smsContent;

    //窗口管理
    private WindowManager wm;
    //布局参数
    private WindowManager.LayoutParams layoutParams;
    //View
    private DispatchLinearLayout mView;

    private TextView tv_phone;
    private TextView tv_content;
    private Button btn_send_sms;

    private HomeWatchReceiver mHomeWatchReceiver;

    public static  final String SYSTEM_DIALOGS_RESON_KEY="reason";
    public static  final String SYSTEM_DIALOGS_HOME_KEY="homekey";

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        init();
    }
    //初始化
    private void init() {
        LogUtil.i("init service");
        //动态注册
        smsReceiver=new SmsReceiver();
        IntentFilter intent=new IntentFilter();
        //添加action
        intent.addAction(StaticClass.SMS_ACTION);
        //设置权限
        intent.setPriority(Integer.MAX_VALUE);
        //注册
        registerReceiver(smsReceiver,intent);

        //home键事件接收器
        mHomeWatchReceiver=new HomeWatchReceiver();
        IntentFilter intentFilter=new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        registerReceiver(mHomeWatchReceiver,intentFilter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtil.i("stop service");
        //注销
        unregisterReceiver(smsReceiver);
        unregisterReceiver(mHomeWatchReceiver);
    }


    //短信广播
    public class SmsReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {

            String action=intent.getAction();
            if(StaticClass.SMS_ACTION.equals(action)){
                LogUtil.i("来短信了");
                //获取短信内容，接受Object数组
                Object[] objs= (Object[]) intent.getExtras().get("pdus");
                //遍历数组得到相关数据
                for(Object obj :objs){
                    //把数组元素转换成短信对象
                    SmsMessage sms=SmsMessage.createFromPdu((byte[]) obj);
                    //发件人
                    smsPhone=sms.getOriginatingAddress();
                    //内容
                    smsContent=sms.getMessageBody();
                    LogUtil.i("短信内容："+smsPhone+": "+smsContent);
                    showWindow();
                }
            }
        }
    }
    //窗口提示
    private void showWindow() {

        //获取系统服务
        wm= (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        //获取布局参数
        layoutParams=new WindowManager.LayoutParams();
        //定义宽高
        layoutParams.width=WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height=WindowManager.LayoutParams.MATCH_PARENT;
        //定义标记
        layoutParams.flags=WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                |WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH;
        //定义格式
        layoutParams.format= PixelFormat.TRANSLUCENT;
        //定义类型
        layoutParams.type=WindowManager.LayoutParams.TYPE_PHONE;

        //加载布局并初始化
        mView= (DispatchLinearLayout) View.inflate(getApplicationContext(), R.layout.sms_item,null);
        tv_phone=mView.findViewById(R.id.tv_phone);
        tv_content=mView.findViewById(R.id.tv_content);
        btn_send_sms=mView.findViewById(R.id.btn_send_sms);
        btn_send_sms.setOnClickListener(this );

        //设置数据
        tv_phone.setText("发件人："+smsPhone);
        tv_content.setText(smsContent);
        LogUtil.i("短信内容："+smsContent);

        //添加View到窗口
        wm.addView(mView,layoutParams);//此处有问题,导致启动服务后接收短信会闪退？？？？？？

        mView.setDispatchKeyEventListener(mDispatchKeyEventListener);
    }
    private DispatchLinearLayout.DispatchKeyEventListener mDispatchKeyEventListener
            =new DispatchLinearLayout.DispatchKeyEventListener() {
        @Override
        public boolean dispatchKeyEvent(KeyEvent event) {
            //判断是否是按返回键
            if(event.getKeyCode()==KeyEvent.KEYCODE_BACK){
                LogUtil.i("按下了返回键");
                if(mView.getParent()!=null){
                    wm.removeView(mView);
                }
                return true;
            }
            return false;
        }
    };
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_send_sms:
                sendSms();
                //让窗消失
                if(mView.getParent()!=null){
                    wm.removeView(mView);
                }
                break;
        }

    }
    //回复短信
    private void sendSms() {
        Uri uri=Uri.parse("smsto:"+smsPhone);
        Intent intent=new Intent(Intent.ACTION_SENDTO,uri);
        //设置启动模式
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("sms_body","");
        startActivity(intent);
    }
    //监听Home键的广播
    class HomeWatchReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            String action=intent.getAction();
            if(action.equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)){
                String reason=intent.getStringExtra(SYSTEM_DIALOGS_RESON_KEY);
                if(SYSTEM_DIALOGS_HOME_KEY.equals(reason)){
                    LogUtil.i("点击了home键");
                    if(mView.getParent()!=null){
                        wm.removeView(mView);
                    }
                }
            }
        }
    }

}
