package com.example.lei.doublekill.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.TextView;

import com.daimajia.numberprogressbar.NumberProgressBar;
import com.example.lei.doublekill.R;
import com.example.lei.doublekill.utils.LogUtil;
import com.kymjs.rxvolley.RxVolley;
import com.kymjs.rxvolley.client.HttpCallback;
import com.kymjs.rxvolley.client.ProgressListener;
import com.kymjs.rxvolley.http.VolleyError;
import com.kymjs.rxvolley.toolbox.FileUtils;

import java.io.File;

public class UpdateActivity extends BaseActivity {

    //正在下载
    public static final int HANDLER_LODING=10001;
    //下载完成
    public static final int HANDLER_OK=10002;
    //下载失败
    public static final int HANDLER_NO=10003;


    private TextView tv_size;
    private String url;
    private String path;

    //进度条
    private NumberProgressBar numberProgressBar;

    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case HANDLER_LODING:
                    //实时更新进度
                    Bundle bundle=msg.getData();
                    long transferredBytes=bundle.getLong("transferredBytes");
                    long totalSize=bundle.getLong("totalSize");
                    tv_size.setText(transferredBytes+"/"+totalSize);
                    //设置进度
                    numberProgressBar.setProgress((int)(((float)transferredBytes/(float)totalSize)*100));
                    break;
                case HANDLER_OK:
                    tv_size.setText(R.string.text_Ddwnload_successful);
                    LogUtil.i("下载成功");
                    //启动安装包
                    startInstallApk();
                    break;
                case HANDLER_NO:
                    tv_size.setText(R.string.text_dwnload_failure);
                    break;

            }
        }
    };
    //启动安装包
    private void startInstallApk() {//不能启动安装程序不知道为啥？？？？？？？？
        Intent i=new Intent();
        i.setAction(Intent.ACTION_VIEW);
        i.addCategory(Intent.CATEGORY_DEFAULT);
        i.setDataAndType(Uri.fromFile(new File(path)),"application/vnd.android.package-archive");
        startActivity(i);
        LogUtil.i("finish");
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);
        initView();
    }

    private void initView() {
        tv_size=findViewById(R.id.tv_size);
        numberProgressBar=findViewById(R.id.number_progress_bar);
        numberProgressBar.setMax(100);
        path=FileUtils.getSDCardPath()+"/1111111111.apk";
        LogUtil.i(path);

        //下载
        url=getIntent().getStringExtra("url");
        if(!TextUtils.isEmpty(url)){

            RxVolley.download(path, url, new ProgressListener() {
                @Override
                public void onProgress(long transferredBytes, long totalSize) {
                    Message msg=new Message();
                    msg.what=HANDLER_LODING;
                    Bundle bundle=new Bundle();
                    bundle.putLong("transferredBytes",transferredBytes);
                    bundle.putLong("totalSize",totalSize);
                    msg.setData(bundle);
                    handler.sendMessage(msg);
                }
            }, new HttpCallback() {
                @Override
                public void onSuccess(String t) {
                   handler.sendEmptyMessage(HANDLER_OK);
                }

                @Override
                public void onFailure(VolleyError error) {
                    handler.sendEmptyMessage(HANDLER_NO);
                }
            });
        }
    }
}
