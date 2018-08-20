package com.example.lei.doublekill.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.lei.doublekill.R;
import com.example.lei.doublekill.adapter.WeChatAdapter;
import com.example.lei.doublekill.entity.WeChatData;
import com.example.lei.doublekill.ui.WebViewActivity;
import com.example.lei.doublekill.utils.LogUtil;
import com.example.lei.doublekill.utils.StaticClass;
import com.kymjs.rxvolley.RxVolley;
import com.kymjs.rxvolley.client.HttpCallback;
import com.kymjs.rxvolley.http.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class WechatFragment extends Fragment {

    private ListView mListView;
    private List<WeChatData> mList=new ArrayList<>();
    //标题
    private List<String> mListTitle=new ArrayList<>();
    //地址
    private List<String> mListUrl=new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_wechat,null);
        findView(view);
        return view;
    }
    //初始化
    private void findView(View view) {
        mListView=view.findViewById(R.id.mListView);
        //解析接口
        String url="http://v.juhe.cn/weixin/query?key=" + StaticClass.WE_CAHT_KEY + "&ps=100";
        //发送请求并接收返回数据
        RxVolley.get(url, new HttpCallback() {
            @Override
            public void onSuccess(String t) {
                LogUtil.i("wechat json:"+t);
                //解析JSON
                parsingJson(t);
            }
            @Override
            public void onFailure(VolleyError error) {
                super.onFailure(error);
                LogUtil.e("weChat"+error.toString());
            }
        });
        //点击事件
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                LogUtil.i("position:"+i);
                Intent intent=new Intent(getActivity(),WebViewActivity.class);
                intent.putExtra("title",mListTitle.get(i));
                intent.putExtra("url",mListUrl.get(i));
                startActivity(intent);
            }
        });

    }

//      返回示例：
//     "reason": "请求成功",
//             "result": {
//        "list": [
//        {
//            "id": "3008050622_2650555965_1",
//                "title": "去了尼泊尔才知道，这里没有寡妇，女人公开沐浴，一生结三次婚！",
//                "source": "创业宝典",
//                "firstImg": "",/*本字段暂无内容，暂不支持封面图片*/
//                "mark": "",
//                "url": "https://mp.weixin.qq.com/s?__biz=MzAwODA1MDYyMg==&mid=2650555965&idx=1&sn=78f81b23be1a800f7030372ecedecadc&chksm=837c24ecb40badfa074ea6e42d259534e26e9961480b00ea5fded0c357ff34021154f79700d4"
//        },
//        {

    //解析JSON
    private void parsingJson(String t) {
        try {
            JSONObject jsonObject=new JSONObject(t);
            JSONObject jsonresult=jsonObject.getJSONObject("result");
            //获取JSON数组
            JSONArray jsonList=jsonresult.getJSONArray("list");
            //解析JSON数组
            for (int i = 0; i <jsonList.length() ; i++) {
                JSONObject json= (JSONObject) jsonList.get(i);

                String title=json.getString("title");
                String url=json.getString("url");

                WeChatData data=new WeChatData();
                data.setTitle(title);
                data.setSource(json.getString("source"));
                data.setImgUrl(json.getString("firstImg"));

                mList.add(data);//用于获取适配器
                //存储title和URL用于传递给webVeiwActivity
                mListTitle.add(title);
                mListUrl.add(url);
            }
            WeChatAdapter adapter=new WeChatAdapter(getActivity(),mList);
            mListView.setAdapter(adapter);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
