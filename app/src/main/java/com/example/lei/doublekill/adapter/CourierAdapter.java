package com.example.lei.doublekill.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.lei.doublekill.R;
import com.example.lei.doublekill.entity.CourierData;
import com.example.lei.doublekill.utils.LogUtil;

import java.util.List;

public class CourierAdapter extends BaseAdapter {
    private Context mContext;
    private List<CourierData> mList;
    //布局加载器
    private LayoutInflater inflater;
    private CourierData data;


    public CourierAdapter (Context mContext,List<CourierData> mList){
        this.mContext=mContext;
        this.mList=mList;
        //获取系统服务
        inflater= (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
        //第一次加载
        if(view==null){
            LogUtil.d("第一次加载");
            viewHolder = new ViewHolder();
            view=inflater.inflate(R.layout.layout_courier_item,null);
            viewHolder.tv_remark=view.findViewById(R.id.tv_remark);
            viewHolder.tv_zone=view.findViewById(R.id.tv_zone);
            viewHolder.tv_datatime=view.findViewById(R.id.tv_datatime);
            //设置缓存
            view.setTag(viewHolder);
        }else {
            LogUtil.d("不是第一次加载");
            viewHolder= (ViewHolder) view.getTag();
        }
        //设置数据
        data=mList.get(i);

        viewHolder.tv_remark.setText(data.getRemark());
        viewHolder.tv_zone.setText(data.getZone());
        viewHolder.tv_datatime.setText(data.getDatatime());
        //返回view
        return view;
    }
    class ViewHolder{
        private TextView tv_remark;
        private TextView tv_zone;
        private TextView tv_datatime;
    }
}
