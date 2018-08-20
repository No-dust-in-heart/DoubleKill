package com.example.lei.doublekill.ui;

import android.support.v7.app.AppCompatActivity;
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

public class CourierActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText et_companyname,et_number;
    private Button btn_get_courier;
    private ListView mListView;

    private List<CourierData> mList=new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_courier);

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
//                            Toast.makeText(CourierActivity.this,t,Toast.LENGTH_SHORT).show();
//                    String t ="{\"resultcode\":\"200\",\"reason\":\"成功的返回\",\"result\":{\"company\":\"圆通\",\"com\":\"yt\",\"no\":\"801026797826130584\",\"status\":\"1\",\"list\":[{\"datetime\":\"2018-08-11 16:35:22\",\"remark\":\"江苏省南京市玄武区公司取件人: 阮宝志（15295710892）已收件\",\"zone\":\"\"},{\"datetime\":\"2018-08-11 20:15:01\",\"remark\":\"快件已发往 南京转运中心\",\"zone\":\"\"},{\"datetime\":\"2018-08-11 21:35:38\",\"remark\":\"快件已到达 南京转运中心\",\"zone\":\"\"},{\"datetime\":\"2018-08-11 22:13:24\",\"remark\":\"快件已发往 郑州转运中心\",\"zone\":\"\"},{\"datetime\":\"2018-08-12 07:54:58\",\"remark\":\"快件已到达 漯河转运中心\",\"zone\":\"\"},{\"datetime\":\"2018-08-12 11:08:34\",\"remark\":\"快件已到达 郑州转运中心\",\"zone\":\"\"},{\"datetime\":\"2018-08-12 13:28:52\",\"remark\":\"快件已发往 河南省焦作市公司\",\"zone\":\"\"},{\"datetime\":\"2018-08-13 06:25:34\",\"remark\":\"快件已到达 河南省焦作市公司\",\"zone\":\"\"},{\"datetime\":\"2018-08-13 07:40:11\",\"remark\":\"快件已发往 河南省焦作市客运总站公司\",\"zone\":\"\"},{\"datetime\":\"2018-08-13 08:18:51\",\"remark\":\"河南省焦作市客运总站公司张**（15978707529） 正在派件 \",\"zone\":\"\"},{\"datetime\":\"2018-08-13 09:20:31\",\"remark\":\"快件已由河南理工大学博雅苑超市南边菜鸟驿站代收，请及时取件，如有疑问请联系18039187673\",\"zone\":\"\"},{\"datetime\":\"2018-08-13 09:55:25\",\"remark\":\"快件已签收 签收人: 抓紧时间取包裹15978707529 感谢使用圆通速递，期待再次为您服务\",\"zone\":\"\"}]},\"error_code\":0}";
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
