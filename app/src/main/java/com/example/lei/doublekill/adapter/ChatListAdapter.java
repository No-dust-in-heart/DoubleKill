package com.example.lei.doublekill.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.lei.doublekill.R;
import com.example.lei.doublekill.entity.ChatListData;
import com.example.lei.doublekill.entity.MyUser;
import com.example.lei.doublekill.utils.UtilTools;

import java.util.List;

import cn.bmob.v3.BmobUser;

public class ChatListAdapter extends BaseAdapter {
    //左边
    public static final int VALUE_LEFT_TEXT=1;
    //右边
    public static final int VALUE_RIGHT_TEXT=2;

    //四大金刚：上下文 布局加载器 实体类 数据源
    //上下文
    private Context mContext;
    //布局加载器
    private LayoutInflater inflater;
    //实体类
    private ChatListData data;
    //数据源
    private List<ChatListData> mList;

    String image,currentuserid;

    //构造器
    public ChatListAdapter(Context mContext,List<ChatListData> mList){
        this.mContext=mContext;
        this.mList=mList;
        //获取系统服务
        inflater= (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        MyUser user= BmobUser.getCurrentUser(MyUser.class);
        if(user!=null){
            image = user.getImageString();
            currentuserid=user.getObjectId();
        }
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
        ViewHolderRightText viewHolderRightText=null;
        ViewHolderLeftText viewHolderLeftText=null;
        //获取当前要显示的type 根据这个type来区分加载子项布局数据
        int type=getItemViewType(i);
        //第一次加载
        if(view==null){
            switch (type){
                case VALUE_LEFT_TEXT:
                    viewHolderLeftText=new ViewHolderLeftText();
                    view =inflater.inflate(R.layout.left_item,null);
                    viewHolderLeftText.tv_Left_text=view.findViewById(R.id.tv_left_text);
                    view.setTag(viewHolderLeftText);
                    break;
                case VALUE_RIGHT_TEXT:
                    viewHolderRightText=new ViewHolderRightText();
                    view =inflater.inflate(R.layout.right_item,null);
                    viewHolderRightText.tv_right_text=view.findViewById(R.id.tv_right_text);
                    viewHolderRightText.user_image=view.findViewById(R.id.user_image);
                    view.setTag(viewHolderRightText);
                    break;
            }
        }else {
            switch (type){
                case VALUE_LEFT_TEXT:
                    viewHolderLeftText= (ViewHolderLeftText) view.getTag();
                    break;
                case VALUE_RIGHT_TEXT:
                    viewHolderRightText= (ViewHolderRightText) view.getTag();
                    break;
            }
        }
        //赋值
        ChatListData data=mList.get(i);
        switch (type){
            case VALUE_LEFT_TEXT:
                viewHolderLeftText.tv_Left_text.setText(data.getText());
                break;
            case VALUE_RIGHT_TEXT:
                viewHolderRightText.tv_right_text.setText(data.getText());
                UtilTools.getImageToShare(mContext,viewHolderRightText.user_image,currentuserid);
                break;
        }
        return view;
    }
    //根据数据源的position来返回要显示的item的类型
    @Override
    public int getItemViewType(int position) {
        ChatListData data=mList.get(position);
        int type=data.getType();
        return type;
    }
    //返回所有的layout数据？？？？？
    @Override
    public int getViewTypeCount() {//????
        return 3;//一般是mList.size+1
    }

    //右边
    class ViewHolderRightText{
        private TextView tv_right_text;
        private ImageView user_image;
    }
    //左边
    class ViewHolderLeftText{
        private TextView tv_Left_text;
    }
}
