package com.example.lei.doublekill.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.lei.doublekill.R;
import com.example.lei.doublekill.entity.MyUser;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText et_user,et_age,et_desc,et_pass;
    private EditText et_password,et_email;
    private RadioGroup mRadioGroup;
    private Button btnRegistered;
    private boolean isboy=true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initView();
    }

    private void initView() {
        et_age=findViewById(R.id.et_age);
        et_desc=findViewById(R.id.et_desc);
        et_user=findViewById(R.id.et_user);
        et_email=findViewById(R.id.et_email);
        et_pass=findViewById(R.id.et_pass);
        et_password=findViewById(R.id.et_password_confirm);
        mRadioGroup=findViewById(R.id.mRadiogroup);
        btnRegistered=findViewById(R.id.btnRegistered);
        btnRegistered.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case  R.id.btnRegistered:
                //获取输入框的值
                String name=et_user.getText().toString().trim();
                String age=et_age.getText().toString().trim();
                String desc=et_desc.getText().toString().trim();
                String pass=et_pass.getText().toString().trim();
                String password=et_password.getText().toString().trim();
                String email=et_email.getText().toString().trim();

                //1.判断是否为空
                if(!TextUtils.isEmpty(name)
                        &!TextUtils.isEmpty(age)
                        &!TextUtils.isEmpty(pass)
                        &!TextUtils.isEmpty(password)
                        &!TextUtils.isEmpty(email)){
                    //2.判断两次密码是否一致
                    if(pass.equals(password)){
                        //3.判断性别
                        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                                if(i==R.id.rb_boy){
                                    isboy=true;
                                }else if(i==R.id.rb_girl){
                                    isboy=false;
                                }
                            }
                        });
                        //4.判断简介是否为空
                        if(TextUtils.isEmpty(desc)){
                            desc="这个人很懒，什么都没有留下";
                        }
                        //6.注册
                        MyUser user=new MyUser();
                        user.setAge(Integer.parseInt(age));
                        user.setUsername(name);
                        user.setPassword(password);
                        user.setEmail(email);
                        user.setSex(isboy);
                        user.setDesc(desc);
                        
                        user.signUp(new SaveListener<MyUser>() {
                            @Override
                            public void done(MyUser myUser, BmobException e) {
                                if(e==null){
                                    Toast.makeText(RegisterActivity.this,"注册成功",Toast.LENGTH_SHORT).show();
                                    finish();
                                }else {
                                    Toast.makeText(RegisterActivity.this,"注册失败",Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                        
                    }else {
                        Toast.makeText(this,"两次密码不一致",Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(this,"输入框不能为空",Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
}
