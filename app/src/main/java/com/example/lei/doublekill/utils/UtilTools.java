package com.example.lei.doublekill.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Base64;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lei.doublekill.R;
import com.example.lei.doublekill.entity.MyUser;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import cn.bmob.v3.BmobUser;
import de.hdodenhof.circleimageview.CircleImageView;

//用于放置常用的相同的的方法
public class UtilTools {

    //设置字体
    public static void setFont(Context mContext, TextView mTextView) {
        //从asset文件夹下获取字体
        Typeface fontType = Typeface.createFromAsset(mContext.getAssets(), "fonts/FONT.TTF");
        mTextView.setTypeface(fontType);
    }

    //保存图片到shareutils
    public static void putImageToShare(Context mContext, ImageView imageView, String key) {
        BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();
        Bitmap bitmap = drawable.getBitmap();

        ByteArrayOutputStream byStream = new ByteArrayOutputStream();
        //第一步：将Bitmap压缩成字节数组输出流
        bitmap.compress(Bitmap.CompressFormat.PNG, 80, byStream);
        //第二步：利用Base64将我们的字节数组输出流转换成String
        byte[] byteArray = byStream.toByteArray();
        String imgString = new String(Base64.encodeToString(byteArray, Base64.DEFAULT));
        //第三步：将String保存shareUtils
        ShareUtils.putString(mContext, key, imgString);
        LogUtil.i("图片字符串" + imgString);
    }

    //读取图片
    public static void getImageToShare(Context mContext, ImageView imageView, String key) {
        //1.从本地拿到string
        String imgString;
        imgString = ShareUtils.getString(mContext, key, "");
        if (TextUtils.isEmpty(imgString)) {
            //1.1从后台拿到string
            LogUtil.i("从后台获取头像");
            //获取当前用户对象
            MyUser myUser=BmobUser.getCurrentUser(MyUser.class);
            imgString = myUser.getImageString();
        }
        if(!TextUtils.isEmpty(imgString)) {
            //2.利用Base64将我们string转换字节数组输出流
            byte[] byteArray = Base64.decode(imgString, Base64.DEFAULT);
            ByteArrayInputStream byStream = new ByteArrayInputStream(byteArray);
            //3.生成bitmap
            Bitmap bitmap = BitmapFactory.decodeStream(byStream);
            imageView.setImageBitmap(bitmap);
        }
    }

    //获取版本号
    public static String getVersion(Context mContext) {
        PackageManager pm = mContext.getPackageManager();
        try {
            PackageInfo info = pm.getPackageInfo(mContext.getPackageName(), 0);
            return info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            return mContext.getString(R.string.text_unknown);
        }
    }
}
