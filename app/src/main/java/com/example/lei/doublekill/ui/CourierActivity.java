package com.example.lei.doublekill.ui;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.lei.doublekill.R;
import com.example.lei.doublekill.adapter.CourierAdapter;
import com.example.lei.doublekill.entity.CourierData;
import com.example.lei.doublekill.utils.LogUtil;
import com.example.lei.doublekill.utils.StaticClass;
import com.kymjs.rxvolley.RxVolley;
import com.kymjs.rxvolley.client.HttpCallback;
import com.kymjs.rxvolley.http.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CourierActivity extends BaseActivity implements View.OnClickListener {
    private EditText et_companyname,et_number;
    private Button btn_get_courier;
    private ListView mListView;

    private List<CourierData> mList=new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_courier);
        //显示Android自带返回键
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("物流查询");
        initView();
    }
    //初始化
    private void initView() {
        et_companyname=findViewById(R.id.et_companyname);
        et_number=findViewById(R.id.et_number);
        btn_get_courier=findViewById(R.id.btn_get_courier);
        mListView=findViewById(R.id.mListView);

        btn_get_courier.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_get_courier:
                //获取输入框内容
                String name=et_companyname.getText().toString().trim();
                String number=et_number.getText().toString().trim();
                //url
                String url="http://v.juhe.cn/exp/index?key="+ StaticClass.COURIER_KEY+"&com="+name+"&no="+number;
//                String url="http://v.juhe.cn/exp/index?key=9828dc49fad67bcd03ca766ee3ed36d8&com=yt&no=801026797826130584;
                //判断是否为空
                if(!TextUtils.isEmpty(name)&&!TextUtils.isEmpty(number)){
                    //请求数据（JSON）
                    RxVolley.get(url, new HttpCallback() {
                        @Override
                        public void onSuccess(String t) {
                            LogUtil.d("json"+t);
                            //解析JSON
                            parsingJson(t);
                        }
                        @Override
                        public void onFailure(VolleyError error) {
                            super.onFailure(error);
                        }
                    });

                }else {
                    Toast.makeText(this,"你不给我信息我去查个毛线啊！？",Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
    //解析数据
    private void parsingJson(String t) {
        try {
            JSONObject jsonObject=new JSONObject(t);
            JSONObject jsonResult=jsonObject.getJSONObject("result");
            JSONArray jsonArray=jsonResult.getJSONArray("list");

            for (int i=0; i<jsonArray.length();i++) {
                JSONObject json= (JSONObject) jsonArray.get(i);
                CourierData data=new CourierData();
                data.setRemark(json.getString("remark"));
                data.setZone(json.getString("zone"));
                data.setDatatime(json.getString("datetime"));
                mList.add(data);
                LogUtil.d("解析中。。。。。。");
            }
            LogUtil.d("解析完成");
            //倒序
            Collections.reverse(mList);
            CourierAdapter adapter =new CourierAdapter(this,mList);
            mListView.setAdapter(adapter);
        } catch (JSONException e) {
            e.printStackTrace();
            LogUtil.d(e.toString());
            Toast.makeText(this ,"异常"+e.toString(),Toast.LENGTH_SHORT).show();
        }
    }
}
