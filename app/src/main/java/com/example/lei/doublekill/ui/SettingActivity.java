package com.example.lei.doublekill.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lei.doublekill.R;
import com.example.lei.doublekill.service.SmsService;
import com.example.lei.doublekill.utils.LogUtil;
import com.example.lei.doublekill.utils.ShareUtils;
import com.example.lei.doublekill.utils.StaticClass;
import com.kymjs.rxvolley.RxVolley;
import com.kymjs.rxvolley.client.HttpCallback;
import com.kymjs.rxvolley.http.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import static com.example.lei.doublekill.utils.ShareUtils.getBoolean;

public class SettingActivity extends BaseActivity implements View.OnClickListener {

    //语音播报
    private Switch sw_speak;
    //短信提醒
    private Switch sw_sms;
    //物流查询
    private LinearLayout ll_courier;
    //归属地查询
    private LinearLayout ll_phone;
    //检测更新
    private LinearLayout ll_update;
    private TextView tv_version;

    private String versionName;
    private int versionCode;
    private String url;

    //扫一扫
    private LinearLayout ll_scan;
    //扫描结果
    private TextView tv_scan_result;
    //生成二维码
    private LinearLayout ll_qr_code;
    //我的位置
    private LinearLayout ll_my_location;
    //关于软件

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        initView();
    }
    //初始化
    private void initView() {
        //语音和短信提示
        sw_speak=findViewById(R.id.sw_speak);
        sw_sms=findViewById(R.id.sw_sms);
        sw_speak.setOnClickListener(this);
        sw_sms.setOnClickListener(this);
        //此处导入了一个静态包,导入此包后以下两种写法效果相同
        boolean isSpeak=getBoolean(this,"isSpeak",false);
        boolean isSms=ShareUtils.getBoolean(this,"isSms",false);
        sw_speak.setChecked(isSpeak);
        sw_sms.setChecked(isSms);

        //去掉阴影
        getSupportActionBar().setElevation(0);
        //物流查询和归属地查询
        ll_courier = findViewById(R.id.ll_courier);
        ll_phone = findViewById(R.id.ll_phone);
        ll_courier.setOnClickListener(this);
        ll_phone.setOnClickListener(this);

        //版本更新
        ll_update=findViewById(R.id.ll_update);
        ll_update.setOnClickListener(this);
        tv_version=findViewById(R.id.tv_version);
        try {
            getVersionNameCode();
            tv_version.setText(getString(R.string.text_test_version)+versionName);
        } catch (PackageManager.NameNotFoundException e) {
            tv_version.setText(getString(R.string.text_test_version));
        }

        //扫一扫
        ll_scan=findViewById(R.id.ll_scan);
        ll_qr_code=findViewById(R.id.ll_qr_code);
        tv_scan_result=findViewById(R.id.tv_scan_result);

        ll_scan.setOnClickListener(this);
        ll_qr_code.setOnClickListener(this);

        //我的位置
        ll_my_location=findViewById(R.id.ll_my_location);
        ll_my_location.setOnClickListener(this);


        //显示Android自带返回键
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.sw_speak:
                //切换至相反状态
                sw_speak.setSelected(!sw_speak.isSelected());
                //保存状态
                ShareUtils.putBoolean(this,"isSpeak",sw_speak.isChecked());
                break;
            case R.id.sw_sms:
                //切换至相反状态
                sw_sms.setSelected(!sw_sms.isSelected());
                //保存状态
                ShareUtils.putBoolean(this,"isSms",sw_sms.isChecked());
                if(sw_sms.isChecked()){
                    startService(new Intent(this,SmsService.class));
                }else {
                    stopService(new Intent(this,SmsService.class));
                }
                break;
            case R.id.ll_update:
                /**
                 * 1.请求服务器的配置文件，拿到code
                 * 2.比较版本号大小
                 * 3.dialog提示
                 * 4.跳转到更新界面，并把URL传递进去
                 */
                showUpdateDialog("仅做测试");
//                RxVolley.get(StaticClass.CHECK_UPDATE_URL, new HttpCallback() {
//                    @Override
//                    public void onSuccess(String t) {
//                        parsingJson(t);
//                        LogUtil.i(t);
//                    }                    @Override
//                    public void onFailure(VolleyError error) {
//                        super.onFailure(error);
//                        LogUtil.i("settingActivity网络错误："+error.toString());
//                    }
//                });
                break;
            case R.id.ll_scan:
                //打开扫描界面扫描条形码或二维码
//                Intent openCameraIntent = new Intent(this, CaptureActivity.class);
//                startActivityForResult(openCameraIntent, 0);
                break;
            case R.id.ll_qr_code:
                //跳转到生成二维码的界面
                startActivity(new Intent(this,QrCodeActivity.class));
                break;
            case R.id.ll_my_location:
                startActivity(new Intent(this,MyLocationActivity.class));
                break;
            case R.id.ll_courier:
                startActivity(new Intent(this, CourierActivity.class));
                break;
            case R.id.ll_phone:
                startActivity(new Intent(this, PhoneActivity.class));
                break;
        }
    }

    private void parsingJson(String t) {
        try {
            JSONObject jsonObject=new JSONObject(t);
            url=jsonObject.getString("url");
            int code=jsonObject.getInt("versionCode");
            if(code>versionCode){
                //若有新版本则弹出升级提示
                showUpdateDialog(jsonObject.getString("context"));
            }else {
                Toast.makeText(this,"当前为最新版本",Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
    //弹出升级提示
    private void showUpdateDialog(String context) {
        new AlertDialog.Builder(this)
                .setTitle("有新版本啦！！")
                .setMessage(context)
                .setPositiveButton("更新", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //跳转到更新界面
                        Intent intent=new Intent(SettingActivity.this,UpdateActivity.class);
                        //为了测试将URL先替换为一个可用apk链接
                        intent.putExtra("url","http://ucan.25pp.com/Wandoujia_web_seo_baidu_homepage.apk");
                        startActivity(intent);
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //会自动执行dismiss方法
            }
        }) .show();
    }

    //获取版本号/Code
    private void getVersionNameCode() throws PackageManager.NameNotFoundException {
        PackageManager pm=getPackageManager();
        PackageInfo info=pm.getPackageInfo(getPackageName(),0);
        versionName=info.versionName;
        versionCode=info.versionCode;
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            Bundle bundle = data.getExtras();
            String scanResult = bundle.getString("result");
            tv_scan_result.setText(scanResult);
        }
    }


}
