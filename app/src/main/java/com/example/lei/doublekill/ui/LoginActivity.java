package com.example.lei.doublekill.ui;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lei.doublekill.MainActivity;
import com.example.lei.doublekill.R;
import com.example.lei.doublekill.entity.MyUser;
import com.example.lei.doublekill.utils.LogUtil;
import com.example.lei.doublekill.utils.ShareUtils;
import com.example.lei.doublekill.view.CustomDialog;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

/**
 * 1.获取信息、判空然后、判断用户信息是否正确,、登录跳转
 * 2.记住密码状态的取值、存值
 * 3.注册和忘记密码页面跳转
 */
public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btnLogin, btnRegister;
    private TextView tv_forget_pass;
    private CheckBox cbRemeberPass;
    private EditText etUser, etPassWord;
    private CustomDialog login_dialog;
    private AlertDialog.Builder builder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();
    }

    private void initView() {

        //用缓存用户登录
        MyUser user = BmobUser.getCurrentUser(MyUser.class);
        if (user != null) {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        } else {
            Toast.makeText(LoginActivity.this, "请重新登录", Toast.LENGTH_SHORT).show();
        }

        //初始化控件
        etUser = findViewById(R.id.et_username);
        etPassWord = findViewById(R.id.et_password);
        btnLogin = findViewById(R.id.btn_login);
        btnRegister = findViewById(R.id.btn_register);
        cbRemeberPass = findViewById(R.id.cb_remeber_pass);
        tv_forget_pass = findViewById(R.id.tv_forget_pass);

        btnLogin.setOnClickListener(this);
        btnRegister.setOnClickListener(this);
        tv_forget_pass.setOnClickListener(this);


        //初始化dialog
        login_dialog = new CustomDialog(this, 300, 300,
                R.layout.dialog_loading, R.style.Theme_dialog, Gravity.CENTER, R.style.pop_anim_style);
        login_dialog.setCancelable(false);

        //获取复选框状态
        boolean isCheck = ShareUtils.getBoolean(this, "keeppass", false);
        cbRemeberPass.setChecked(isCheck);
        if (isCheck) {
            //还原用户名和密码
            etUser.setText(ShareUtils.getString(this, "name", ""));
            etPassWord.setText(ShareUtils.getString(this, "password", ""));
        }
    }

    private boolean NetworkStatus() {
        /** 获得系统级联网管理员对象 */
        ConnectivityManager manager = (ConnectivityManager) this
                .getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo info = manager.getActiveNetworkInfo();
        if (info == null) { //无网情况下
            LogUtil.i("没有联网");
//            builder.setTitle("网络错误")
//                    .setMessage("网络未连接是否查看网络状态?")
//                    .setPositiveButton("确定",new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialogInterface, int i) {
//                            // 跳转到网络设置页面
//                            startActivity(new Intent(android.provider.Settings.ACTION_SETTINGS));
//                        }
//                    }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialogInterface, int i) {
//                    //nothing
//                }
//            }).show();

//            Toast.makeText(this,"网络未连接",Toast.LENGTH_SHORT).show();
            return false;
        } else { // 有网情况下
            return true;
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_login:
                //获取输入的值
                String name = etUser.getText().toString().trim();
                String password = etPassWord.getText().toString().trim();
                LogUtil.d(name + "  " + password);
                //判断是否为空
                if (!TextUtils.isEmpty(name) & !TextUtils.isEmpty(password)) {
                    login_dialog.show();//弹出对话框
                    //Bmob登录
                    final MyUser user = new MyUser();
                    user.setUsername(name);
                    user.setPassword(password);
                    user.login(new SaveListener<MyUser>() {
                        @Override
                        public void done(MyUser myUser, BmobException e) {
                            login_dialog.dismiss();//对话框消失
                            //判断登录结果
                            /**
                             * 1.不知道为啥总是登录失败,错误码101??????
                             * 因为你注册的时候把年龄当密码设置进去了
                             * 2.纠正上述bug后,依然登录失败,文档说是AndroidSdk的错误码9015含义:其他错误????日志如下:
                             *errorCode:9015,java.lang.NullPointerException: Attempt to invoke virtual method
                             * 'boolean java.lang.Boolean.booleanValue()' on a null object reference
                             */
                            if ((e == null || e.getErrorCode() == 9015)&NetworkStatus()) {
                                //判断邮箱是否验证,没有开启邮箱功能邮箱验证结果为null
                                if (user.getEmailVerified() != null) {
                                    if (user.getEmailVerified()) {
                                        //此段代码执行不了
                                        LogUtil.i(user.getEmailVerified() + "");
                                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                        Toast.makeText(LoginActivity.this, "此功能未开启,所以这个是不可能弹出来的", Toast.LENGTH_SHORT).show();
                                        finish();
                                    }
                                } else {
                                    Toast.makeText(LoginActivity.this, "请前往邮箱验证", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                    finish();
                                }
                            } else {
                                Toast.makeText(LoginActivity.this, "登录失败", Toast.LENGTH_SHORT).show();
                                LogUtil.e(e.toString());
                            }
                            //bug消除后删掉,以下两行代码会导致在用户信息输入错误的情况下闪退，原因：UserFragment Java 96行
//                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
//                            finish();
                        }
                    });
                } else {
                    Toast.makeText(this, "输入框不能为空", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btn_register:
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
                break;
            case R.id.tv_forget_pass:
                startActivity(new Intent(LoginActivity.this, ForgetPasswordActivity.class));
                break;
        }
    }

    @Override
    protected void onDestroy() {//用户输入信息后即使信息错误直接退出依然会保存信息

        //保存复选框状态,便于下次获取
        ShareUtils.putBoolean(this, "keeppass", cbRemeberPass.isChecked());
        //是否记住密码
        if (cbRemeberPass.isChecked()) {
            //记住用户名和密码
            ShareUtils.putString(this, "name", etUser.getText().toString().trim());
            ShareUtils.putString(this, "password", etPassWord.getText().toString().trim());
        } else {
            ShareUtils.deleShare(this, "name");
            ShareUtils.deleShare(this, "password");
        }
        super.onDestroy();
    }
}
