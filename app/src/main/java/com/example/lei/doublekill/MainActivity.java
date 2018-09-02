package com.example.lei.doublekill;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import com.example.lei.doublekill.fragment.ButlerFrament;
import com.example.lei.doublekill.fragment.GirlFragment;
import com.example.lei.doublekill.fragment.UserFragment;
import com.example.lei.doublekill.fragment.WechatFragment;
import com.example.lei.doublekill.ui.SettingActivity;
import com.example.lei.doublekill.utils.LogUtil;
import com.tencent.bugly.crashreport.CrashReport;

import java.util.ArrayList;
import java.util.List;

/**用于管理viewpager的各个fragment
 * 1.初始化fragment和title的List集合
 * 2.监听viewpager，从而有选择的加载title和fragment
 * 3.FloatingActionButton的设置页面跳转及不同fragment的可见情况判断
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    //Tablayout
    private TabLayout mTabLayout;
    //ViewPager
    private ViewPager mViewPager;
    //Title
    private List<String> mTitle;
    //Fragment
    private List<Fragment> mFragment;
    //悬浮窗
    private FloatingActionButton fab_setting;
    //时间标记
    long timeTag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //去掉阴影
        getSupportActionBar().setElevation(0);
        //隐藏actionBar
        getSupportActionBar().hide();
        //初始化
        initData();
        initView();
        //Bugly测试
//        CrashReport.testJavaCrash();
    }

    //初始化View
    @SuppressLint("RestrictedApi")//不加这个fab_setting.setVisibility下面会有红线但不影响运行
    private void initView() {
        fab_setting=findViewById(R.id.fab_setting);
        fab_setting.setOnClickListener(this);
        fab_setting.setVisibility(View.GONE);
        mTabLayout = findViewById(R.id.mTabLayout);
        mViewPager = findViewById(R.id.mViewPager);

        //设置预加载页卡数量
        mViewPager.setOffscreenPageLimit(mFragment.size());
        //mViewPager的滑动监听
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {
            }
            @Override
            public void onPageSelected(int i) {
                LogUtil.i("position"+i);
                if(i==0){
                    fab_setting.setVisibility(View.GONE);
                }else{
                    fab_setting.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
        //设置适配器，fragment适配器
        mViewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            //选中的item,即加载fragment
            @Override
            public Fragment getItem(int i) {
                return mFragment.get(i);
            }
            //返回item的个数
            @Override
            public int getCount() {
                return mFragment.size();
            }
            //设置标题
            @Nullable
            @Override
            public CharSequence getPageTitle(int position) {
                return mTitle.get(position);
            }
        });

        //绑定TabLayout和ViewPager
        mTabLayout.setupWithViewPager(mViewPager);
    }

    //初始化
    private void initData() {
        mTitle = new ArrayList<>();
        mTitle.add("服务管家");
        mTitle.add("美女图片");
        mTitle.add("微信精选");
        mTitle.add("个人中心");

        mFragment = new ArrayList<>();
        mFragment.add(new ButlerFrament());
        mFragment.add(new GirlFragment());
        mFragment.add(new WechatFragment());
        mFragment.add(new UserFragment());
    }
    //实现双击退出应用
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN)
        {
            if((System.currentTimeMillis()-timeTag) > 2000)  //System.currentTimeMillis()无论何时调用，肯定大于2000
            {
                Toast.makeText(getApplicationContext(), "再按一次退出程序",Toast.LENGTH_SHORT).show();
                timeTag = System.currentTimeMillis();
            }
            else
            {
                finish();
//                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.fab_setting:
                startActivity(new Intent(this,SettingActivity.class));
                break;
        }

    }
}
