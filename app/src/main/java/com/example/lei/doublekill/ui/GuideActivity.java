package com.example.lei.doublekill.ui;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.example.lei.doublekill.MainActivity;
import com.example.lei.doublekill.R;
import com.example.lei.doublekill.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;

public class GuideActivity extends AppCompatActivity implements View.OnClickListener {

    private ViewPager mViewPager;
    //view容器
    private List<View> mList=new ArrayList<>();
    private View view1,view2,view3;
    //小圆点
    private ImageView point1,point2,point3;
    //跳过
    private ImageView iv_back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);
        initView();
    }
    //初始化
    private void initView() {
        //加载视图
        view1=View.inflate(this,R.layout.pager_item_one,null);
        view2=View.inflate(this,R.layout.pager_item_two,null);
        view3=View.inflate(this,R.layout.pager_item_three,null);
        mList.add(view1);
        mList.add(view2);
        mList.add(view3);
        //跳过按钮
        iv_back=findViewById(R.id.iv_back);
        iv_back.setOnClickListener(this);
        //进入主页按钮
        view3.findViewById(R.id.btn_start).setOnClickListener(this);
        //viewPager控件
        mViewPager=findViewById(R.id.mViewPager);
        //圆点图片
        point1=findViewById(R.id.point1);
        point2=findViewById(R.id.point2);
        point3=findViewById(R.id.point3);
        //设置圆点默认状态
        setPointImg(true,false,false);

        //设置适配器
        mViewPager.setAdapter(new GuideAdapter());
        //监听viewPager的滑动,用于改变圆点状态
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }
            @Override
            public void onPageSelected(int i) {
                LogUtil.i("position"+i);
                switch (i){
                    case 0:
                        setPointImg(true,false,false);
                        iv_back.setVisibility(View.VISIBLE);
                        break;
                    case 1:
                        setPointImg(false,true,false);
                        iv_back.setVisibility(View.VISIBLE);
                        break;
                    case 2:
                        setPointImg(false,false,true);
                        iv_back.setVisibility(View.GONE);
                        break;
                }
            }
            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_start:
            case R.id.iv_back:
                startActivity(new Intent(this, MainActivity.class));
                finish();
                break;
        }
    }
    //适配器内部类
    class GuideAdapter extends PagerAdapter {
         @Override
         public int getCount() {
             return mList.size();
         }
         //?????
         @Override
         public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
             return view==o;
         }
         @NonNull
         @Override//实例化item
         public Object instantiateItem(@NonNull ViewGroup container, int position) {
             ((ViewPager)container).addView(mList.get(position));
             return mList.get(position);
         }
         @Override//销毁item
         public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
             ((ViewPager)container).removeView(mList.get(position));
         }
     }
     //设置小圆点的选中效果
    private void setPointImg(boolean isCheck1,boolean isCheck2,boolean isCheck3){
        if(isCheck1){
            //动态加载图片资源
            point1.setBackgroundResource(R.drawable.point_on);
        }else {
            point1.setBackgroundResource(R.drawable.point_off);
        }
        if(isCheck2){
            point2.setBackgroundResource(R.drawable.point_on);
        }else {
            point2.setBackgroundResource(R.drawable.point_off);
        }
        if(isCheck3){
            point3.setBackgroundResource(R.drawable.point_on);
        }else {
            point3.setBackgroundResource(R.drawable.point_off);
        }
    }
}
