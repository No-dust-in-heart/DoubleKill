package com.example.lei.doublekill.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.lei.doublekill.R;
import com.example.lei.doublekill.entity.WeChatData;
import com.example.lei.doublekill.utils.LogUtil;
import com.example.lei.doublekill.utils.PicassoUtils;
import com.squareup.picasso.Picasso;

import java.util.List;

public class WeChatAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater inflater;
    private List<WeChatData> mList;
    private WeChatData data;
    //
    private int width,height;
    private WindowManager wm;
    //构造器
    public WeChatAdapter(Context mContext,List<WeChatData> mList){
        this.mContext=mContext;
        this.mList=mList;
        //获取布局加载器
        inflater= (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //
        wm= (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        width=wm.getDefaultDisplay().getWidth();
        height=wm.getDefaultDisplay().getHeight();
        LogUtil.i("Width"+width+"Height:"+height);
    }
    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int i) {
        return mList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        ViewHolder viewHolder=null;
        if(view==null){//第一次加载，找到控件加载布局
            viewHolder=new ViewHolder();
            view=inflater.inflate(R.layout.wechat_item,null);
            viewHolder.iv_img=view.findViewById(R.id.iv_img);
            viewHolder.tv_source=view.findViewById(R.id.tv_source);
            viewHolder.tv_title=view.findViewById(R.id.tv_title);
            //缓存viewholder
            view.setTag(viewHolder);
        }else {
            viewHolder= (ViewHolder) view.getTag();
        }
        //给数据赋值
        data=mList.get(i);
        viewHolder.tv_title.setText(data.getTitle());
        viewHolder.tv_source.setText(data.getSource());

        if(!TextUtils.isEmpty(data.getImgUrl())){//判空,接口返回的imgUrl为空，不怪我！
            //加载图片
            LogUtil.i("weChatAdapter"+data.getImgUrl());
            PicassoUtils.loadImageViewSize(mContext,data.getImgUrl(),viewHolder.iv_img,width/3,200);
        }else {
            Picasso.get().load("https://wallhalla.com/thumbs/preview/0/0nYKabESEEX.jpg")
                    //设置默认和出错情况下的图片
                    .placeholder(R.drawable.backgril)
                    .error(R.drawable.backgril)
                    //更改图片大小
                    .config(Bitmap.Config.RGB_565)
                    .resize(width/3,200)
                    .centerCrop()
                    //设置加载目标
                    .into(viewHolder.iv_img);
        }
        return view;
    }
    class ViewHolder{
        private ImageView iv_img;
        private TextView tv_title;
        private TextView tv_source;
    }
}
