package com.example.lei.doublekill.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.lei.doublekill.R;
import com.example.lei.doublekill.adapter.GridAdapter;
import com.example.lei.doublekill.entity.GirlData;
import com.example.lei.doublekill.utils.LogUtil;
import com.example.lei.doublekill.utils.PicassoUtils;
import com.example.lei.doublekill.view.CustomDialog;
import com.github.chrisbanes.photoview.PhotoView;
import com.github.chrisbanes.photoview.PhotoViewAttacher;
import com.kymjs.rxvolley.RxVolley;
import com.kymjs.rxvolley.client.HttpCallback;
import com.kymjs.rxvolley.http.VolleyError;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;
import com.scwang.smartrefresh.layout.footer.BallPulseFooter;
import com.scwang.smartrefresh.layout.header.BezierRadarHeader;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class GirlFragment extends Fragment {

    //列表
    private GridView mGridView;
    //数据
    private List<GirlData> mList=new ArrayList<>();
    //适配器
    private GridAdapter mAdapter;
    //提示框
    private CustomDialog dialog;
    //预览图片
    private PhotoView iv_img;
    //PhotoView
    private PhotoViewAttacher mAttacher;
    //图片的地址数据
    public static List<String> mListUrl=new ArrayList<>();
    //下拉刷新和上拉加载
    RefreshLayout refreshLayout;
    //用于判断是刷新、加载还是初始化
    private static int tag=0;
    //初始图片数量
    private int num=50;
    /**
     * 1.监听点击事件
     * 2.提示框
     * 3.加载图片
     * 4.PhotoView
     * 5.下拉刷新和上拉加载
     */

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_gril,null);
        findView(view);
        return view;
    }

    //初始化
    private void findView(final View view) {

        mGridView=view.findViewById(R.id.mGridView);

        //dialog形式的预览图片
        dialog=new CustomDialog(getActivity(), LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,R.layout.dialog_girl,
                R.style.Theme_dialog, Gravity.CENTER,R.style.pop_anim_style);
        iv_img=dialog.findViewById(R.id.iv_img);
        //请求并接收返回数据
        requestAndReceive();

        //刷新与加载
        refreshLayout = view.findViewById(R.id.refreshLayout);
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                LogUtil.i("下拉刷新");
                tag=1;
                num=50;
                mList.clear();
                mListUrl.clear();
                requestAndReceive();
                refreshlayout.finishRefresh(/*,false*/);//传入false表示刷新失败
            }
        });
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(RefreshLayout refreshlayout) {
                LogUtil.i("上拉加载");
                tag=2;
                requestAndReceive();
                refreshlayout.finishLoadMore();//传入false表示加载失败
            }
        });
        //设置 Header 为 贝塞尔雷达 样式
        refreshLayout.setRefreshHeader(new BezierRadarHeader(getActivity()).setEnableHorizontalDrag(true));
        //设置 Footer 为 球脉冲 样式
        refreshLayout.setFooterHeight(50);
        refreshLayout.setFooterTriggerRate(1f);
        refreshLayout.setRefreshFooter( new BallPulseFooter(getActivity()).setSpinnerStyle(SpinnerStyle.Scale));
//        String welfare=null;
//        try {
//            //Gank升级 需要转码?????????????
//            welfare = URLEncoder.encode(getString(R.string.text_welfare),"UTF-8");
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }

        //监听点击事件
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //加载图片
                PicassoUtils.loadImageView(getActivity(),mListUrl.get(i),iv_img);
                LogUtil.i("图片位置为："+i+"mListurl大小："+mListUrl.size()+"mlist大小"+mList.size());
                //缩放
                mAttacher=new PhotoViewAttacher(iv_img);
                //刷新
                mAttacher.update();
                dialog.show();
            }
        });
    }

    //请求与接受数据
    private void requestAndReceive() {

        if(tag==2){//说明调用者是上拉加载loadmore
            num+=20;
            LogUtil.i("num为"+num);
        }
        //拼接URL
        String url="https://gank.io/api/data/福利/"+num+"/1";
        //解析
        RxVolley.get(url, new HttpCallback() {
            @Override
            public void onSuccess(String t) {
                LogUtil.i("Gril Json:"+t);
                parsinJson(t);
            }
            @Override
            public void onFailure(VolleyError error) {
                LogUtil.e("girlfragment:"+error.toString());
                super.onFailure(error);
                Toast.makeText(getActivity(),"加载失败",Toast.LENGTH_SHORT).show();
            }
        });
    }

    //解析JSON
    private void parsinJson(String t) {
        try {
            JSONObject jsonObject=new JSONObject(t);
            JSONArray jsonArray =jsonObject.getJSONArray("results");
            for (int i = 0; i <jsonArray.length() ; i++) {
                if(tag==2) {//当loadmore时
                    if(i>=num-20) {//仅仅解析并加载新增的20个
                        JSONObject json= (JSONObject) jsonArray.get(i);
                        String url=json.getString("url");
                        GirlData data=new GirlData();
                        data.setImgurl(url);
                        mListUrl.add(url);
                        mList.add(data);
                    }
                }else {//当第一次加载或刷新时
                    JSONObject json= (JSONObject) jsonArray.get(i);
                    String url=json.getString("url");
                    GirlData data=new GirlData();
                    data.setImgurl(url);
                    mListUrl.add(url);
                    mList.add(data);
                }
            }
            if(tag==0) {//第一次加载时
                mAdapter = new GridAdapter(getActivity(), mList);
                mGridView.setAdapter(mAdapter);
            }else {//下拉刷新或上拉加载
                mAdapter.notifyDataSetChanged();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
