package halfdog.bupt.edu.bubbledating.adapter;

import android.content.Context;
import android.os.IInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import halfdog.bupt.edu.bubbledating.R;
import halfdog.bupt.edu.bubbledating.entity.BubbleDatingApplication;
import halfdog.bupt.edu.bubbledating.entity.ChatMsgEntity;

/**
 * Created by andy on 2015/5/5.
 */
public class ChatMsgAdapter extends BaseAdapter {
    private final String TAG = "ChatMsgAdapter";
    private static Context context;
    private static final int IM_SEND = 0;
    private static final int IM_RECEIVE = 1;

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
        for(int i = 0; i < data.size(); i++ ){
            Log.d(TAG,"-->data["+i+"].isReceive:"+data.get(i).isReceive());
        }

    }

    public int getMsgTypeCount() {
        return IM_TYPE_COUNT;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ChatMsgEntity entity = data.get(position);
        boolean receive = entity.isReceive();
//        int mMsgType = getMsgType(position);
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            if (!receive) {
                // send msg
                convertView = inflater.inflate(R.layout.chat_msg_text_left, null);
//                holder.isReceive = receive;
            } else {
                // receive msg
                convertView = inflater.inflate(R.layout.chat_msg_text_right, null);
//                holder.isReceive = receive;
            }
            holder.mUserHead = (ImageView) convertView.findViewById(R.id.chat_msg_text_head);
            holder.mPostTime = (TextView) convertView.findViewById(R.id.chat_msg_text_post_time);
            holder.mUserName = (TextView) convertView.findViewById(R.id.chat_msg_text_name);
            holder.mContent = (TextView) convertView.findViewById(R.id.chat_msg_text_content);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.mUserHead.setImageDrawable(context.getResources().getDrawable(R.drawable.default_user_head));
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
