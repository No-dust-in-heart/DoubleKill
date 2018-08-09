package com.example.lei.doublekill.utils;

import android.content.Context;
import android.graphics.Typeface;
import android.widget.TextView;

public class UtilTools {
    //设置字体
    public static void setFont(Context mContext, TextView mTextView){
        //从asset文件夹下获取字体
        Typeface fontType=Typeface.createFromAsset(mContext.getAssets(),"fonts/FONT.TTF");
        mTextView.setTypeface(fontType);
    }
}
