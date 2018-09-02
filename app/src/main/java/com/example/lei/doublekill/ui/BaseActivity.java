package com.example.lei.doublekill.ui;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.example.lei.doublekill.utils.LogUtil;

//Activity基类,主要做统一的接口,方法,属性
public class BaseActivity extends AppCompatActivity {
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        //去掉阴影
        getSupportActionBar().setElevation(0);
        LogUtil.i("baseActivity");
        //显示Android自带返回键
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
    //选项菜单
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home://android自带的返回键id？？？
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}
