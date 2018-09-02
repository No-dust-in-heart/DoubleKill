package com.example.lei.doublekill.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.example.lei.doublekill.R;
import com.example.lei.doublekill.entity.GirlData;
import com.example.lei.doublekill.utils.PicassoUtils;

import java.util.List;

public class GridAdapter extends BaseAdapter {

    private Context mContext;
    private List<GirlData> mList;
    private LayoutInflater inflater;
    private GirlData data;

    private WindowManager wm;
    private int width;

    public GridAdapter(Context mContext,List<GirlData> mList){
        this.mContext=mContext;
        this.mList=mList;
        inflater= (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //获取屏幕的宽
        wm= (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        width=wm.getDefaultDisplay().getWidth();
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
        viewHolder viewHolder;
        if(view==null){//第一次加载，初始化数据、加载布局、设置缓存
            viewHolder=new viewHolder();
            view=inflater.inflate(R.layout.girl_item,null);
            viewHolder.imageView=view.findViewById(R.id.imageview);
            view.setTag(viewHolder);
        }else {
            viewHolder= (GridAdapter.viewHolder) view.getTag();
        }
        //加载数据
        data=mList.get(i);
        String url=data.getImgurl();
        PicassoUtils.loadImageViewSize(mContext,url, viewHolder.imageView,width/2,500);
        return view;
    }

    class viewHolder{
        private ImageView imageView;
    }
}
