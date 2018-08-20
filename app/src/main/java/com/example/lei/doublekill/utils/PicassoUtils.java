package com.example.lei.doublekill.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

public class PicassoUtils {

    //直接加载图片
    public static void loadImageView(Context mContext, String url, ImageView imageView){
        Picasso.get().load(url)
                .into(imageView);
    }
    //加载图片并指定大小
    public static void
    loadImageViewSize(Context mContext, String url,ImageView imageView,int width,int height){
        Picasso.get().load(url)
                .config(Bitmap.Config.RGB_565)
                .resize(width,height)
                .centerCrop()
                .into(imageView);
    }
    //设置默认图片、出错情况下的图片、要加载的图片
    public static void
    loadImageViewHolder(Context mContext, String url,ImageView imageView,int loadImg,int errorImg){
        Picasso.get().load(url)
                .placeholder(loadImg)
                .error(errorImg)
                .into(imageView);
    }
    //裁剪图片
    public static void loadImageViewCrop(Context mContext, String url,ImageView imageView){

        Picasso.get().load(url).transform(new Transformation() {
            @Override //按照比例裁剪图片
            public Bitmap transform(Bitmap source) {
                int size=Math.min(source.getWidth(),source.getHeight());
                int x=(source.getWidth() -size)/2;
                int y=(source.getHeight() -size)/2;
                Bitmap result=Bitmap.createBitmap(source,x,y,size,size);
                if(result!=source){
                    //回收
                    source.recycle();
                }
                return result;
            }

            @Override
            public String key() {//标志
                return "leige";
            }
        }).into(imageView);
    }

}
