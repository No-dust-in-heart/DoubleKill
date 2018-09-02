package com.example.lei.doublekill.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lei.doublekill.R;
import com.example.lei.doublekill.entity.MyUser;
import com.example.lei.doublekill.ui.CourierActivity;
import com.example.lei.doublekill.ui.LoginActivity;
import com.example.lei.doublekill.ui.PhoneActivity;
import com.example.lei.doublekill.utils.LogUtil;
import com.example.lei.doublekill.utils.ShareUtils;
import com.example.lei.doublekill.utils.StaticClass;
import com.example.lei.doublekill.utils.UtilTools;
import com.example.lei.doublekill.view.CustomDialog;

import java.io.FileNotFoundException;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;
import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;

/**
 *
 * 2.编辑框状态切换，确认修改按钮的显示隐藏
 * 3.用户退出登录和信息的修改
 * 4.圆形头像
 * 5.修改头像的dialog
 * 6.跳转相机、跳转相册
 * 7.图片裁剪
 */

public class UserFragment extends Fragment implements View.OnClickListener {

    private Button btn_update_ok, btn_exit_user;
    private TextView edit_user;
    private EditText et_age, et_sex, et_name, et_desc;

    //对话框按钮
    private Button btn_camera, btn_picture, btn_cancel;
    private CustomDialog dialog;
    //圆形头像
    private CircleImageView profile_image;
    //裁减之后的图片uri
    private Uri uriTempFile;
    //用户对象
    private MyUser user;
    private String currentUserId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user, null);
        findView(view);
        return view;
    }

    //初始化
    private void findView(View view) {
        btn_exit_user = view.findViewById(R.id.btn_exit_user);
        btn_update_ok = view.findViewById(R.id.btn_update_ok);
        edit_user = view.findViewById(R.id.edit_user);

        et_age = view.findViewById(R.id.et_age);
        et_sex = view.findViewById(R.id.et_sex);
        et_name = view.findViewById(R.id.et_name);
        et_desc = view.findViewById(R.id.et_desc);
        profile_image = view.findViewById(R.id.profile_image);

        btn_exit_user.setOnClickListener(this);
        btn_update_ok.setOnClickListener(this);
        edit_user.setOnClickListener(this);
        profile_image.setOnClickListener(this);

        //初始化选择头像dialog
        dialog = new CustomDialog(getActivity(), LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT, R.layout.dialog_photo,
                R.style.Theme_dialog, Gravity.BOTTOM, R.style.pop_anim_style);

        //提示框以外点击取消
        dialog.setCancelable(true);

        btn_camera = dialog.findViewById(R.id.btn_camera);
        btn_picture = dialog.findViewById(R.id.btn_picture);
        btn_cancel = dialog.findViewById(R.id.btn_cancel);
        btn_camera.setOnClickListener(this);
        btn_picture.setOnClickListener(this);
        btn_cancel.setOnClickListener(this);

        //使得编辑框默认不可编辑
        setEnabled(false);
        //设置具体值
        if(BmobUser.getCurrentUser()!=null) {//退出登录
            //获取用户头像
            currentUserId = BmobUser.getCurrentUser().getObjectId();
            LogUtil.i("用户ID" + currentUserId);
            UtilTools.getImageToShare(getActivity(), profile_image, currentUserId);
            MyUser userInfo = BmobUser.getCurrentUser(MyUser.class);
            et_name.setText(userInfo.getUsername());
            et_age.setText(userInfo.getAge() + "");
            et_sex.setText(userInfo.isSex() ? "男" : "女");
            et_desc.setText(userInfo.getDesc());
        }

    }

    //控制编辑框及头像状态
    private void setEnabled(boolean b) {
        et_name.setEnabled(b);
        et_age.setEnabled(b);
        et_sex.setEnabled(b);
        et_desc.setEnabled(b);
        profile_image.setEnabled(b);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_update_ok:
                //拿到输入框的值
                String username = et_name.getText().toString();
                String age = et_age.getText().toString();
                String sex = et_sex.getText().toString();
                String desc = et_desc.getText().toString();
                //判空
                if (!TextUtils.isEmpty(username) & !TextUtils.isEmpty(age)
                        & !TextUtils.isEmpty(sex)) {
                    //更新资料
                    user = new MyUser();
                    user.setUsername(username);
                    user.setAge(Integer.parseInt(age));
                    user.setImageString(ShareUtils.getString(getActivity(), currentUserId, ""));
                    //性别
                    if (sex.equals("男")) {
                        user.setSex(true);
                    } else if (sex.equals("女")) {
                        user.setSex(false);
                    } else {
                        Toast.makeText(getActivity(), "修改失败！性别只能填男或女！！！", Toast.LENGTH_SHORT).show();
                        setEnabled(false);
                        btn_update_ok.setVisibility(View.GONE);
                        break;
                    }
                    //简介
                    if (!TextUtils.isEmpty(desc)) {
                        user.setDesc(desc);
                    } else {
                        user.setDesc("这个人很懒，什么都没有留下！");
                    }
                    BmobUser bmobUser = BmobUser.getCurrentUser();
                    //相当于先填写一个用户的信息然后将此用户信息导入到当前用户
                    user.update(bmobUser.getObjectId(), new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            if (e == null) {
                                //修改成功
                                setEnabled(false);
                                btn_update_ok.setVisibility(View.GONE);
                                Toast.makeText(getActivity(), "修改成功", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getActivity(), "修改失败" + e.toString(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    Toast.makeText(getActivity(), "输入框不能为空", Toast.LENGTH_SHORT).show();
                }
                break;
            //退出登录
            case R.id.btn_exit_user:
                //清除缓存用户对象
                MyUser.logOut();
                //现在的currentUser是null了
//                BmobUser currentUser=MyUser.getCurrentUser();
                startActivity(new Intent(getActivity(), LoginActivity.class));
                getActivity().finish();
                break;
            case R.id.edit_user:
                //使得编辑框处于可编辑状态
                setEnabled(true);
                //显现出确认修改按钮
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
        }

    }

    public static final String PHOTO_IMAGE_FILE_NAME = "fileImg.jpg";
    public static final int CAMERA_REQUEST_CODE = 100;
    public static final int IMAGE_REQUEST_CODE = 101;
    public static final int RESULT_REQUEST_CODE = 102;

    //跳转相册
    private void toPicture() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_REQUEST_CODE);
        LogUtil.d("成功进入相册");
        dialog.dismiss();
    }
    //跳转相机
    private void toCamera() {
        LogUtil.d("toCamera");
        //这样拍完后获得的照片会严重失真
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA_REQUEST_CODE);
        dialog.dismiss();
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode != getActivity().RESULT_CANCELED) {
            switch (requestCode) {
                //相册
                case IMAGE_REQUEST_CODE:
                    LogUtil.d("前往裁剪");
                    if(data!=null) {//解决进相册之后点击“取消”会闪退的问题,原因是此种情况下返回的intent为null
                        startPhotoZoom(data.getData());
                    }
                    break;
                //相机
                case CAMERA_REQUEST_CODE:
                    if ( resultCode == RESULT_OK) {
                        Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                        //将bitmap转化为uri
                        Uri uri = Uri.parse(MediaStore.Images.Media.insertImage(
                                getActivity().getContentResolver(), bitmap, null,null));
                        //裁剪
                        startPhotoZoom(uri);
                        //不裁剪直接设置
//                        profile_image.setImageBitmap(bm);
                    }
                    break;
                case RESULT_REQUEST_CODE:
                    //有可能点击舍弃
                    if (data != null) {
                        //设置图片
                        setImageToView();
                    }
                    break;
            }
        }
    }

    //设置图片
    private void setImageToView() {
        //将Uri图片转换为Bitmap
        try {
            Bitmap bitmap = BitmapFactory.decodeStream(getActivity().getContentResolver().openInputStream(uriTempFile));
            profile_image.setImageBitmap(bitmap);
            //将图片变成String保存到本地
            UtilTools.putImageToShare(getActivity(), profile_image,currentUserId);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
//        Bundle bundle = data.getExtras();
//        if (bundle != null) {
//            LogUtil.i("setImageToView开始设置图片");
//            Bitmap bitmap = bundle.getParcelable("data");
//            //裁剪之后设置保存图片的路径
//            String path = getActivity().getFilesDir().getPath() + File.separator + IMAGE_FILE_NAME;
//            //压缩图片
////            ImageUtils.saveImage(photo, path);
//            Drawable drawable = new BitmapDrawable(bitmap);
//            profile_image.setImageDrawable(drawable);
//            profile_image.setImageBitmap(bitmap);
//        }
    }

    //裁剪
    private void startPhotoZoom(Uri uri) {
        if (uri == null) {//判空
            LogUtil.e("uri ==null");
            return;
        }
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        //设置裁剪
        intent.putExtra("crop", "true");
        //裁剪的宽高比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        //裁剪图片的质量
        intent.putExtra("outputX", 320);
        intent.putExtra("outputY", 320);

        //return-data方法返回的图片只能是小图片（sumsang测试为高宽160px的图片）
        //故只保存图片Uri，调用时将Uri转换为Bitmap，此方法还可解决miui系统不能return data的问题
        //intent.putExtra("return-data", true);

        //裁剪后的图片Uri路径，uritempFile为Uri类变量
        uriTempFile = Uri.parse("file://" + "/" + Environment.getExternalStorageDirectory().getPath() + "/" + PHOTO_IMAGE_FILE_NAME);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uriTempFile);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        //返回码
        startActivityForResult(intent, RESULT_REQUEST_CODE);
        LogUtil.d("裁剪完成");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        StaticClass.LASTUSERID=currentUserId;
    }
}
