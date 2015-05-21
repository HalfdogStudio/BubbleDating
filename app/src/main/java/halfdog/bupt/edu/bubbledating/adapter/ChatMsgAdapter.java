package halfdog.bupt.edu.bubbledating.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import halfdog.bupt.edu.bubbledating.R;
import halfdog.bupt.edu.bubbledating.BubbleDatingApplication;
import halfdog.bupt.edu.bubbledating.entity.ChatMsgEntity;

/**
 * Created by andy on 2015/5/5.
 */
public class ChatMsgAdapter extends BaseAdapter {
    private final String TAG = "ChatMsgAdapter";
    private static Context context;
    private static final int IM_SEND = 10;
    private static final int IM_RECEIVE = 11;

    private static final int IM_TYPE_COUNT = 2;


    private List<ChatMsgEntity> data;
    private LayoutInflater inflater;

    public ChatMsgAdapter(Context context, List<ChatMsgEntity> data) {
        this.context = context;
        this.data = data;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public int getMsgType(int postion) {
        ChatMsgEntity entity = data.get(postion);
        if (entity.isReceive()) {
            return IM_RECEIVE;
        } else {
            return IM_SEND;
        }
    }

    public void refreshData( List<ChatMsgEntity> data){
        this.data = data;
        notifyDataSetChanged();
//        for(int i = 0; i < data.size(); i++ ){
//            Log.d(TAG,"-->data["+i+"].isReceive:"+data.get(i).isReceive());
//        }

    }

    public int getMsgTypeCount() {
        return IM_TYPE_COUNT;
    }

    public View createViewByMsgEntity(int position, ChatMsgEntity msgEntity ){
        return msgEntity.isReceive()?inflater.inflate(R.layout.chat_msg_text_right,null):
                inflater.inflate(R.layout.chat_msg_text_left,null);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ChatMsgEntity entity = data.get(position);
        boolean receive = entity.isReceive();
        ViewHolder holder = null;
        // convertView 为空， 或者 convertView 没有ID， 或者 convertView 的 id 与 所需类型不同
        if ( convertView == null || convertView.getId() == View.NO_ID || convertView.getId() !=
                (receive?R.id.chat_msg_text_right:R.id.chat_msg_text_left)){
            Log.d(TAG,"-->"+position+" convertView is null;");
            holder = new ViewHolder();
            if (!receive) {
                // send msg
                convertView = inflater.inflate(R.layout.chat_msg_text_left, null);
                convertView.setId(R.id.chat_msg_text_left);
            } else {
                // receive msg
                convertView = inflater.inflate(R.layout.chat_msg_text_right, null);
                convertView.setId(R.id.chat_msg_text_right);
            }
            holder.mUserHead = (ImageView) convertView.findViewById(R.id.chat_msg_text_head);
            holder.mPostTime = (TextView) convertView.findViewById(R.id.chat_msg_text_post_time);
            holder.mUserName = (TextView) convertView.findViewById(R.id.chat_msg_text_name);
            holder.mContent = (TextView) convertView.findViewById(R.id.chat_msg_text_content);
            convertView.setTag(holder);
        } else {
            Log.d(TAG,"-->"+position +" convertView is not null;");
            holder = (ViewHolder) convertView.getTag();
        }

        holder.mUserHead.setImageDrawable(context.getResources().getDrawable(R.drawable.avatar_default_m));
        holder.mPostTime.setText(entity.getDate());
        holder.mContent.setText(entity.getContent());
//        holder.mUserName.setText(entity.getName());
        if (!receive){
            holder.mUserName.setText(BubbleDatingApplication.userEntity.getmName());
        }else{
            holder.mUserName.setText(entity.getName());
        }

        return convertView;
    }

    private static class ViewHolder {
        ImageView mUserHead;
        TextView mContent;
        TextView mUserName;
        TextView mPostTime;
        boolean isReceive;
    }
}
