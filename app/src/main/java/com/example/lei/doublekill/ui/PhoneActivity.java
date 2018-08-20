package com.example.lei.doublekill.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lei.doublekill.R;
import com.example.lei.doublekill.utils.LogUtil;
import com.example.lei.doublekill.utils.StaticClass;
import com.kymjs.rxvolley.RxVolley;
import com.kymjs.rxvolley.client.HttpCallback;

import org.json.JSONException;
import org.json.JSONObject;

public class PhoneActivity extends AppCompatActivity implements View.OnClickListener {
    //输入框
    private EditText et_number;
    //图片
    private ImageView iv_company;
    //结果
    private TextView tv_result;
    //键盘按钮
    private Button btn_0,btn_1,btn_2,btn_3,btn_4,btn_5,btn_6,btn_7,btn_8,btn_9,btn_del,btn_query;
    //标记位，用于每次点击查询后的下一次输入前将输入框置空
    private boolean flag=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone);
        initView();
    }
    //初始化View
    private void initView() {
        et_number=findViewById(R.id.et_number);
        iv_company=findViewById(R.id.iv_company);
        tv_result=findViewById(R.id.tv_result);
        btn_0=findViewById(R.id.btn_0);
        btn_0.setOnClickListener(this);
        btn_1=findViewById(R.id.btn_1);
        btn_1.setOnClickListener(this);
        btn_2=findViewById(R.id.btn_2);
        btn_2.setOnClickListener(this);
        btn_3=findViewById(R.id.btn_3);
        btn_3.setOnClickListener(this);
        btn_4=findViewById(R.id.btn_4);
        btn_4.setOnClickListener(this);
        btn_5=findViewById(R.id.btn_5);
        btn_5.setOnClickListener(this);
        btn_6=findViewById(R.id.btn_6);
        btn_6.setOnClickListener(this);
        btn_7=findViewById(R.id.btn_7);
        btn_7.setOnClickListener(this);
        btn_8=findViewById(R.id.btn_8);
        btn_8.setOnClickListener(this);
        btn_9=findViewById(R.id.btn_9);
        btn_9.setOnClickListener(this);
        btn_del=findViewById(R.id.btn_del);
        btn_del.setOnClickListener(this);
        btn_query=findViewById(R.id.btn_query);
        btn_query.setOnClickListener(this);

        //长按事件
        btn_del.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                et_number.setText("");
                return false;
            }
        });
    }

    @Override
    public void onClick(View view) {
        //从编辑框获取号码
        String str=et_number.getText().toString();
        switch (view.getId()){
            case R.id.btn_0:
            case R.id.btn_1:
            case R.id.btn_2:
            case R.id.btn_3:
            case R.id.btn_4:
            case R.id.btn_5:
            case R.id.btn_6:
            case R.id.btn_7:
            case R.id.btn_8:
            case R.id.btn_9:
                if(flag){//若flag为true则说明输入框已有的号码应当清除
                    flag=false;
                    str="";
                    et_number.setText("");
                }
                //每次结尾加1个字符，相应str也会加1个字符
                et_number.setText(str+((Button)view).getText());
                //移动光标
                et_number.setSelection(str.length()+1);
                break;
            case R.id.btn_del:
                if(!TextUtils.isEmpty(str)&&str.length()>0) {
                    //每次结尾减一个字符
                    et_number.setText(str.substring(0, str.length() - 1));
                    //移动光标
                    et_number.setSelection(str.length() - 1);
                }
                break;
            case R.id.btn_query:
                if(!TextUtils.isEmpty(str)){
                    getPhone(str);
                }else {
                    Toast.makeText(this,"输入框不能为空",Toast.LENGTH_SHORT).show();
                }
                break;
        }

    }
    //获取归属地
    private void getPhone(String str) {
        String url="http://apis.juhe.cn/mobile/get?phone="+str+"&key="+ StaticClass.PHONE_KEY;
        RxVolley.get(url, new HttpCallback() {
            @Override
            public void onSuccess(String t) {
                LogUtil.d("phone"+t);
                parsingJson(t);
            }
        });
    }
    //解析JSON
    private void parsingJson(String t) {
        try {
            JSONObject jsonObject=new JSONObject(t);
            JSONObject jsonResult=jsonObject.getJSONObject("result");

            String province =jsonResult.getString("province");
            String city =jsonResult.getString("city");
            String areacode =jsonResult.getString("areacode");
            String zip =jsonResult.getString("zip");
            String company =jsonResult.getString("company");

            tv_result.setText(
                    "运营商："+company+"\n"
                    +"归属地："+province+city+"\n"
                    +"区号："+areacode+"\n"
                    +"邮编："+zip
                    );
            //图片显示
            switch (company){
                case "移动":
                    iv_company.setBackgroundResource(R.drawable.china_mobile);
                    break;
                case "联通":
                    iv_company.setBackgroundResource(R.drawable.china_unicom);
                    break;
                case "电信":
                    iv_company.setBackgroundResource(R.drawable.china_telecom);
                    break;
            }
            flag=true;
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
