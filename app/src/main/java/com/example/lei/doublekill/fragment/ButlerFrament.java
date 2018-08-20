package com.example.lei.doublekill.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.lei.doublekill.R;
import com.example.lei.doublekill.adapter.ChatListAdapter;
import com.example.lei.doublekill.entity.ChatListData;
import com.example.lei.doublekill.utils.LogUtil;
import com.example.lei.doublekill.utils.ShareUtils;
import com.example.lei.doublekill.utils.StaticClass;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SynthesizerListener;
import com.kymjs.rxvolley.RxVolley;
import com.kymjs.rxvolley.client.HttpCallback;
import com.kymjs.rxvolley.http.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ButlerFrament extends Fragment implements View.OnClickListener {
    private ListView mChatListView;
    private List<ChatListData> mList=new ArrayList<>();
    private ChatListAdapter adapter;

    //用于判断文本来源
    public  boolean VOIC_ORIGIN=false;
    //机器人返回的内容文本
    private String huida;
    //TTS
    private SpeechSynthesizer mTts;
    //输入框
    private EditText et_text;
    //发送按钮
    private Button btn_send;
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_butler,null);
        findView(view);
        return view;
    }

    private void findView(View view) {
        //1.创建SpeechSynthesizer对象, 第二个参数：本地合成时传InitListener
        mTts=SpeechSynthesizer.createSynthesizer(getActivity(),null);
        //2.合成参数设置，详见《科大讯飞MSC API手册(Android)》SpeechSynthesizer 类
        mTts.setParameter(SpeechConstant.VOICE_NAME, "xiaoyan");//设置发音人
        mTts.setParameter(SpeechConstant.SPEED, "50");//设置语速
        mTts.setParameter(SpeechConstant.VOLUME, "15");//设置音量，范围0~100
        mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD); //设置云端
        //设置合成音频保存位置（可自定义保存位置），保存在“./sdcard/iflytek.pcm”
        //保存在SD卡需要在AndroidManifest.xml添加写SD卡权限
        //如果不需要保存合成音频，注释该行代码
        //mTts.setParameter(SpeechConstant.TTS_AUDIO_PATH, "./sdcard/iflytek.pcm");

        //初始化控件
        mChatListView=view.findViewById(R.id.mChatListView);
        et_text=view.findViewById(R.id.et_text);
        btn_send=view.findViewById(R.id.btn_send);
        btn_send.setOnClickListener(this);

        //设置适配器
        adapter=new ChatListAdapter(getActivity(),mList);
        mChatListView.setAdapter(adapter);
        addLeftItem(getString(R.string.text_hello_tts));
    }



    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_send:
                //获取输入框内容
                String text=et_text.getText().toString();
                //判断是否为空
                if(!TextUtils.isEmpty(text)){
                    //判断长度是否大于30
                    if(text.length()>30){
                        Toast.makeText(getActivity(),R.string.text_more_length,Toast.LENGTH_SHORT).show();
                    }else {
                        //清空输入框
                        et_text.setText("");
                        //添加内容到right item
                        addRightItem(text);
                        //将内容发送给机器人并获取返回内容
                        String url="http://op.juhe.cn/robot/index?info=" + text
                                + "&key=" + StaticClass.CHAT_LIST_KEY;
                        RxVolley.get(url, new HttpCallback() {
                            @Override
                            public void onSuccess(String t) {
                                LogUtil.i("json"+t);
                                parsingJson(t);
                            }

                            @Override
                            public void onFailure(VolleyError error) {
                                Toast.makeText(getActivity(),"网络错误"+error.toString(),Toast.LENGTH_SHORT).show();
                                super.onFailure(error);
                            }
                        });
                    }

                }else {
                    Toast.makeText(getActivity(),R.string.text_tost_empty,Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
    //解析JSON
    private void parsingJson(String t) {
        try {
            JSONObject jsonObject=new JSONObject(t);
            JSONObject jsonresult=jsonObject.getJSONObject("result");
            //拿到机器人返回值
            huida=jsonresult.getString("text");
            //拿到返回值之后显示出来
//            addLeftItem(huida);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
    //添加左边文本
    private void addLeftItem(String string) {
        ChatListData data=new ChatListData();
        data.setType(ChatListAdapter.VALUE_LEFT_TEXT);
        data.setText(string);
        mList.add(data);
        //通知adapter刷新
        adapter.notifyDataSetChanged();
        //滚动到底部，使得每次显示的都是最新的聊天内容
        mChatListView.setSelection(mChatListView.getBottom());

        //将VOIC_ORIGIN置false，表示文本来自机器人
        VOIC_ORIGIN=false;
        if(ShareUtils.getBoolean(getActivity(),"isSpeak",false)){
            startSpeak(string);
        }

    }
    //添加右边文本
    private void addRightItem(String string){
        ChatListData data=new ChatListData();
        data.setType(ChatListAdapter.VALUE_RIGHT_TEXT);
        data.setText(string);
        mList.add(data);
        //通知adapter刷新
        adapter.notifyDataSetChanged();
        //滚动到底部
        mChatListView.setSelection(mChatListView.getBottom());
        if(ShareUtils.getBoolean(getActivity(),"isSpeak",false)){
            startSpeak(string);
        }
        //将VOIC_ORIGIN置true，表示文本来自用户
        VOIC_ORIGIN=true;
    }
    //开始说话
    private void startSpeak(String text){
        //开始合成
        mTts.startSpeaking(text,mSynListener );
    }

    //合成监听器
    private SynthesizerListener mSynListener=new SynthesizerListener() {

        @Override //开始播放
        public void onSpeakBegin() {

        }
        //缓冲进度回调
        //i为缓冲进度0~100，i1为缓冲音频在文本中开始位置，i2表示缓冲音频在文本中结束位置，s为附加信息。
        @Override
        public void onBufferProgress(int i, int i1, int i2, String s) {
        }
        //暂停播放
        @Override
        public void onSpeakPaused() {

        }

        @Override//恢复播放回调接口
        public void onSpeakResumed() {

        }
        //播放进度回调
        //percent为播放进度0~100,beginPos为播放音频在文本中开始位置，endPos表示播放音频在文本中结束位置.
        @Override
        public void onSpeakProgress(int i, int i1, int i2) {

        }

        @Override //会话结束回调接口，没有错误时，error为null
        public void onCompleted(SpeechError speechError) {

            //判断文本来源，如果是用户，则将文本读出来后再显示并读出返回内容
            if(VOIC_ORIGIN){
                addLeftItem(huida);
            }

        }

        @Override//会话事件回调接口
        public void onEvent(int i, int i1, int i2, Bundle bundle) {

        }
    };
}
