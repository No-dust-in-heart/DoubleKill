package com.example.lei.doublekill.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lei.doublekill.R;
import com.example.lei.doublekill.entity.MyUser;
import com.example.lei.doublekill.ui.CourierActivity;
import com.example.lei.doublekill.ui.LoginActivity;
import com.example.lei.doublekill.ui.PhoneActivity;
import com.example.lei.doublekill.utils.LogUtil;
import com.example.lei.doublekill.utils.UtilTools;
import com.example.lei.doublekill.view.CustomDialog;

import java.io.File;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;
import de.hdodenhof.circleimageview.CircleImageView;


public class UserFragment extends Fragment implements View.OnClickListener {
    private Button btn_update_ok,btn_exit_user;
    private TextView edit_user,tv_courier,tv_phone;
    private EditText et_age,et_sex,et_name,et_desc;

    //对话框按钮
    private Button btn_camera,btn_picture,btn_cancel;
    //圆形头像
    private CircleImageView profile_image;
    private CustomDialog dialog;

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
        tv_courier=view.findViewById(R.id.tv_courier);
        tv_phone=view.findViewById(R.id.tv_phone);
        et_age=view.findViewById(R.id.et_age);
        et_sex=view.findViewById(R.id.et_sex);
        et_name=view.findViewById(R.id.et_name);
        et_desc=view.findViewById(R.id.et_desc);
        profile_image=view.findViewById(R.id.profile_image);

        btn_exit_user.setOnClickListener(this);
        btn_update_ok.setOnClickListener(this);
        edit_user.setOnClickListener(this);
        tv_courier.setOnClickListener(this);
        tv_phone.setOnClickListener(this);
        profile_image.setOnClickListener(this);

        //初始化dialog
        dialog=new CustomDialog(getActivity(),0,0,R.layout.dialog_photo,
                R.style.pop_anim_style, Gravity.BOTTOM,0);

        UtilTools.putImageToShare(getActivity(),profile_image);
        //提示框以外点击无效
        dialog.setCancelable(false);
        btn_camera=dialog.findViewById(R.id.btn_camera);
        btn_picture=dialog.findViewById(R.id.btn_picture);
        btn_cancel=dialog.findViewById(R.id.btn_cancel);
        btn_camera.setOnClickListener(this);
        btn_picture.setOnClickListener(this);
        btn_cancel.setOnClickListener(this);

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
            case R.id.profile_image:
                dialog.show();
                break;
            case R.id.btn_cancel:
                dialog.dismiss();
                break;
            case R.id.btn_camera:
                toCamera();
                break;
            case R.id.btn_picture:
                toPicture();
                break;
            case R.id.tv_courier:
                startActivity(new Intent(getActivity(),CourierActivity.class));
                break;
            case R.id.tv_phone:
                startActivity(new Intent(getActivity(),PhoneActivity.class));
                break;
        }

    }
    public static final String PHOTO_IMAGE_FILE_NAME="fileImg.jpg";
    public static final int CAMERA_REQUEST_CODE=100;
    public static final int IMAGE_REQUEST_CODE=101;
    public static final int RESULT_REQUEST_CODE=102;
    private File tempFile =null;
    //跳转相册
    private void toPicture() {
        Intent intent=new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent,IMAGE_REQUEST_CODE);
        LogUtil.d("成功进入相册");
        dialog.dismiss();
    }
    //跳转相机
    private void toCamera() {
        LogUtil.d("toCamera");
        Intent intent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //判断内存卡是否可用
        intent.putExtra(MediaStore.EXTRA_OUTPUT,
                Uri.fromFile(new File(Environment.getExternalStorageDirectory(),PHOTO_IMAGE_FILE_NAME)));
        startActivityForResult(intent,CAMERA_REQUEST_CODE);//此处报错
        dialog.dismiss();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode !=getActivity().RESULT_CANCELED){
            switch(requestCode){
                //相册
                case IMAGE_REQUEST_CODE:
                    startPhotoZoom(data.getData());
                    LogUtil.d("前往裁剪");
                    break;
                //相机
                case CAMERA_REQUEST_CODE:
                    tempFile=new File(Environment.getExternalStorageDirectory(),PHOTO_IMAGE_FILE_NAME);
                    startPhotoZoom(Uri.fromFile(tempFile));
                    break;
                case RESULT_REQUEST_CODE:
                    //有可能点击舍弃
                    if(data !=null){
                        //图片设置
                        setImageToView(data);
                        //将之前的删除
                        if(tempFile!=null){
                            tempFile.delete();
                        }
                    }
                    break;
            }

        }
    }
    //设置图片
    private void setImageToView(Intent data) {
        Bundle bundle=data.getExtras();
        if(bundle!=null){
            Bitmap bitmap=bundle.getParcelable("data");
            profile_image.setImageBitmap(bitmap);
        }
    }
    //裁剪
    private void startPhotoZoom(Uri uri) {
        if(uri ==null){
            LogUtil.d("uri ==null");
            return;
        }
        Intent intent=new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri,"image/*");
        //设置裁剪
        intent.putExtra("crop","true");
        //裁剪的宽高比例
        intent.putExtra("aspectX",1);
        intent.putExtra("aspectY",1);
        //裁剪图片的质量
        intent.putExtra("outputX",320);
        intent.putExtra("outputY",320);
        //发送数据
        intent.putExtra("return-data",true);
        LogUtil.d("裁剪完成");
        startActivityForResult(intent,RESULT_REQUEST_CODE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //保存
        UtilTools.putImageToShare(getActivity(),profile_image);
    }
}
