package com.example.lei.doublekill.view;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

import com.example.lei.doublekill.R;
import com.example.lei.doublekill.utils.LogUtil;


public class CustomDialog extends Dialog {
    //定义模板
    public CustomDialog(@NonNull Context context,int layout,int style) {
        this(context, WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT,layout,style, Gravity.CENTER);

    }
    //定义属性
    public CustomDialog(@NonNull Context context, int width,int height,int layout,int style,int gravity,int anim) {
       super(context, style);
       //设置属性
        setContentView(layout);
        Window window=getWindow();//获取窗口
        WindowManager.LayoutParams layoutParams=window.getAttributes();//获取窗口属性
        layoutParams.width=width;
        layoutParams.height=height;
        layoutParams.gravity=gravity;
        window.setAttributes(layoutParams);
        window.setWindowAnimations(anim);
    }
    //实例
    public CustomDialog(@NonNull Context context,int width,int height,int layout,int style,int gravity) {
        this(context,width,height,layout,style,gravity,R.style.pop_anim_style);
    }
}
