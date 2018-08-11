package com.example.lei.doublekill.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lei.doublekill.R;
import com.example.lei.doublekill.entity.MyUser;
import com.example.lei.doublekill.ui.LoginActivity;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;


public class UserFragment extends Fragment implements View.OnClickListener {
    private Button btn_update_ok,btn_exit_user;
    private TextView edit_user;
    private EditText et_age,et_sex,et_name,et_desc;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_user,null);
        findView(view);
        return view;
    }
    //初始化
    private void findView(View view) {
        btn_exit_user=view.findViewById(R.id.btn_exit_user);
        btn_update_ok=view.findViewById(R.id.btn_update_ok);
        edit_user=view.findViewById(R.id.edit_user);
        et_age=view.findViewById(R.id.et_age);
        et_sex=view.findViewById(R.id.et_sex);
        et_name=view.findViewById(R.id.et_name);
        et_desc=view.findViewById(R.id.et_desc);

        btn_exit_user.setOnClickListener(this);
        btn_update_ok.setOnClickListener(this);
        edit_user.setOnClickListener(this);

        //使得编辑框默认不可编辑
        setEnabled(false);

        //设置具体值
        MyUser userInfo= BmobUser.getCurrentUser(MyUser.class);
        et_name.setText(userInfo.getUsername());
        et_age.setText(userInfo.getAge()+"");
        et_sex.setText(userInfo.isSex()? "男":"女");
        et_desc.setText(userInfo.getDesc());
    }
    //控制焦点
    private void setEnabled(boolean b) {
        et_name.setEnabled(b);
        et_age.setEnabled(b);
        et_sex.setEnabled(b);
        et_desc.setEnabled(b);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_update_ok:
                //拿到输入框的值
                String username=et_name.getText().toString();
                String age=et_age.getText().toString();
                String sex=et_sex.getText().toString();
                String desc=et_desc.getText().toString();

                //判空
                if(!TextUtils.isEmpty(username) & !TextUtils.isEmpty(age)
                        &!TextUtils.isEmpty(sex) ){
                    //更新资料
                    MyUser user=new MyUser();
                    user.setUsername(username);
                    user.setAge(Integer.parseInt(age));
                    //性别
                    if(sex.equals("男")){
                        user.setSex(true);
                    }else if(sex.equals("女")){
                        user.setSex(false);
                    }else {
                        Toast.makeText(getActivity(),"你是认真的吗？",Toast.LENGTH_SHORT).show();
                    }
                    //简介
                    if (!TextUtils.isEmpty(desc)){
                        user.setDesc(desc);
                    }else{
                        user.setDesc("这个人很懒，什么都没有留下！");
                    }
                    BmobUser bmobUser=BmobUser.getCurrentUser();
                    user.update(bmobUser.getObjectId(), new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            if(e==null){
                                //修改成功
                                setEnabled(false);
                                btn_update_ok.setVisibility(View.GONE);
                                Toast.makeText(getActivity(),"修改成功",Toast.LENGTH_SHORT).show();
                            }else {
                                Toast.makeText(getActivity(),"修改失败"+e.toString(),Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }else {
                    Toast.makeText(getActivity(),"输入框不能为空",Toast.LENGTH_SHORT).show();
                }
                break;
                //退出登录
            case R.id.btn_exit_user:
                //清除缓存用户对象
                MyUser.logOut();
                //现在的currentUser是null了
                BmobUser currentUser=MyUser.getCurrentUser();
                startActivity(new Intent(getActivity(), LoginActivity.class));
                getActivity().finish();
                break;
            case R.id.edit_user:
                setEnabled(true);
                btn_update_ok.setVisibility(View.VISIBLE);
                break;
        }

    }
}
