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
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.lei.doublekill.R;
import com.example.lei.doublekill.adapter.GridAdapter;
import com.example.lei.doublekill.entity.GirlData;
import com.example.lei.doublekill.utils.LogUtil;
import com.example.lei.doublekill.utils.PicassoUtils;
import com.example.lei.doublekill.view.CustomDialog;
import com.github.chrisbanes.photoview.PhotoViewAttacher;
import com.kymjs.rxvolley.RxVolley;
import com.kymjs.rxvolley.client.HttpCallback;
import com.kymjs.rxvolley.http.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
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
    private ImageView iv_img;
    //图片的地址数据
    private List<String> mListUrl=new ArrayList<>();
    //PhotoView
    private PhotoViewAttacher mAttacher;
    /**
     * 1.监听点击事件
     * 2.提示框
     * 3.加载图片
     * 4.PhotoView
     */

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_gril,null);
        findView(view);
        return view;
    }
    //初始化
    private void findView(View view) {

        mGridView=view.findViewById(R.id.mGridView);
        //提示框
        dialog=new CustomDialog(getActivity(), LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT,R.layout.dialog_girl,
                R.style.Theme_dialog, Gravity.CENTER,R.style.pop_anim_style);
        iv_img=dialog.findViewById(R.id.iv_img);
//
//        String welfare=null;
//        try {
//            //Gank升级 需要转码?????????????
//            welfare = URLEncoder.encode(getString(R.string.text_welfare),"UTF-8");
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
        //拼接URL
        String url="https://gank.io/api/data/福利/50/1";
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
            }
        });

        //监听点击事件
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //加载图片
                PicassoUtils.loadImageView(getActivity(),mListUrl.get(i),iv_img);
                //缩放
                mAttacher=new PhotoViewAttacher(iv_img);//缩放功能无法实现？？？？？？？？？
                //刷新
                mAttacher.update();
                dialog.show();
            }
        });
    }
    //解析JSON
    private void parsinJson(String t) {
        try {
            JSONObject jsonObject=new JSONObject(t);
            JSONArray jsonArray =jsonObject.getJSONArray("results");
            for (int i = 0; i <jsonArray.length() ; i++) {
                JSONObject json= (JSONObject) jsonArray.get(i);
                String url=json.getString("url");
                mListUrl.add(url);
                GirlData data=new GirlData();
                data.setImgurl(url);
                mList.add(data);
            }
            mAdapter=new GridAdapter(getActivity(),mList);
            mGridView.setAdapter(mAdapter);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
