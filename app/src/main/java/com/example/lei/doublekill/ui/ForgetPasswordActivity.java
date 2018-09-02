package com.example.lei.doublekill.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.lei.doublekill.R;
import com.example.lei.doublekill.entity.MyUser;
import com.example.lei.doublekill.utils.LogUtil;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;

/**
 * 1.修改密码：获取输入信息、判空、判断两次密码是否一致
 * 2.重置密码：获取输入邮箱、判空、发送邮件
 */
public class ForgetPasswordActivity extends BaseActivity implements View.OnClickListener {
    private EditText et_new,et_now,et_new_password,et_email;
    private Button btn_update_password,btn_forget_password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initView();
    }
    private void initView() {
        et_new=findViewById(R.id.et_new);
        et_now=findViewById(R.id.et_now);
        et_new_password=findViewById(R.id.et_new_password);
        et_email=findViewById(R.id.et_email);
        btn_update_password=findViewById(R.id.btn_update_password);
        btn_forget_password=findViewById(R.id.btn_forget_password);

        btn_forget_password.setOnClickListener(this);
        btn_update_password.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_forget_password:
                //获取输入框的邮箱
                final String email=et_email.getText().toString().trim();
                //判断是否为空
                if(!TextUtils.isEmpty(email)){
                    //发送邮件
                    MyUser.resetPasswordByEmail(email, new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            if(e==null){
                                Toast.makeText(ForgetPasswordActivity.this,
                                        "邮件已发送",Toast.LENGTH_SHORT).show();
                                finish();
                            }else {
                                Toast.makeText(ForgetPasswordActivity.this,
                                        "邮件发送失败",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }else {
                    Toast.makeText(ForgetPasswordActivity.this,
                            "邮箱不能为空",Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btn_update_password:
                //获取输入框的值
                String now=et_now.getText().toString().trim();
                String news=et_new.getText().toString().trim();
                String new_password=et_new_password.getText().toString().trim();
                //判断是否为空
                if(!TextUtils.isEmpty(now)&!TextUtils.isEmpty(news)&!TextUtils.isEmpty(new_password)){
                    //判断两次密码是否一致
                    if(news.equals(new_password)){
                        //重置密码
                            /**
                             * 此处重置密码只需提供旧密码即可重置,那么系统怎么判断用户想修改的是哪个????
                             * 还是说用户密码不允许有重复?????
                             * 答案：currentuser
                             */
                        MyUser.updateCurrentUserPassword(now, news, new UpdateListener() {
                            @Override
                            public void done(BmobException e) {
                                if(e==null){
                                    Toast.makeText(ForgetPasswordActivity.this,
                                            "密码重置成功",Toast.LENGTH_SHORT).show();
                                    finish();
                                }else {
                                    Toast.makeText(ForgetPasswordActivity.this,
                                            "密码重置失败"+e.toString(),Toast.LENGTH_SHORT).show();
                                    LogUtil.d(e.toString());
                                }
                            }
                        });
                    }else {
                        Toast.makeText(ForgetPasswordActivity.this,
                                "两次密码输入不一致",Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(ForgetPasswordActivity.this,
                            "输入框不能为空",Toast.LENGTH_SHORT).show();
                }
                break;
        }

    }
}
