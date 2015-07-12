package halfdog.bupt.edu.bubbledating.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;

import java.util.List;

import halfdog.bupt.edu.bubbledating.R;
import halfdog.bupt.edu.bubbledating.BubbleDatingApplication;
import halfdog.bupt.edu.bubbledating.cache.image.ImageCacheManager;
import halfdog.bupt.edu.bubbledating.constants.Configurations;
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


    public void refreshData( List<ChatMsgEntity> data){
        this.data = data;
        notifyDataSetChanged();

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
        int type = getItemViewType(position);
        ViewHolder holder = null;
        String ownerServerImgPath = Configurations.SERVER_IMG_CACHE_DIR  + BubbleDatingApplication.userEntity.getmName()+".png";
        String targetServerImgPath = null;
        String target = null;
        if(type == IM_RECEIVE){
            target  = entity.getmFrom();
            targetServerImgPath = Configurations.SERVER_IMG_CACHE_DIR  + entity.getmFrom()+".png";
        }else{
            target = entity.getTo();
            targetServerImgPath = Configurations.SERVER_IMG_CACHE_DIR  + entity.getTo()+".png";
        }



        // convertView 为空， 或者 convertView 没有ID， 或者 convertView 的 id 与 所需类型不同
        if ( convertView == null ){
//            Log.d(TAG,"-->"+position+" convertView is null;");
            holder = new ViewHolder();
            if (type == IM_SEND) {
                // send msg
                convertView = inflater.inflate(R.layout.chat_msg_text_left, null);
            } else {
                // receive msg
                convertView = inflater.inflate(R.layout.chat_msg_text_right, null);
            }
            holder.mUserHead = (ImageView) convertView.findViewById(R.id.chat_msg_text_head);
            holder.mPostTime = (TextView) convertView.findViewById(R.id.chat_msg_text_post_time);
            holder.mUserName = (TextView) convertView.findViewById(R.id.chat_msg_text_name);
            holder.mContent = (TextView) convertView.findViewById(R.id.chat_msg_text_content);
            convertView.setTag(holder);
        } else {
//            Log.d(TAG,"-->"+position +" convertView is not null;");
            holder = (ViewHolder) convertView.getTag();
        }

//        ImageLoader.ImageListener userAvatorListener = ImageLoader.getImageListener( holder.mUserHead,
//                R.drawable.avatar_default_m, R.drawable.avatar_default_m);
//        ImageCacheManager.getInstance().getImage(serverImgPath,userAvatorListener);
//        holder.mUserHead.setImageDrawable(context.getResources().getDrawable(R.drawable.avatar_default_m));
        holder.mPostTime.setText(entity.getDate());
        holder.mContent.setText(entity.getContent());
//        holder.mUserName.setText(entity.getName());
        if (type == IM_SEND){
            holder.mUserName.setText(BubbleDatingApplication.userEntity.getmName());
            ImageLoader.ImageListener ownerAvatorListener = ImageLoader.getImageListener( holder.mUserHead,
                    R.drawable.avatar_default_m, R.drawable.avatar_default_m);
            ImageCacheManager.getInstance().getImage(ownerServerImgPath, ownerAvatorListener);
        }else{
            holder.mUserName.setText(target);
            ImageLoader.ImageListener userAvatorListener = ImageLoader.getImageListener( holder.mUserHead,
                    R.drawable.avatar_default_m, R.drawable.avatar_default_m);
            ImageCacheManager.getInstance().getImage(targetServerImgPath, userAvatorListener);
        }

        return convertView;
    }

    @Override
    public int getItemViewType(int position) {
        ChatMsgEntity entity = data.get(position);
        boolean isReceive = entity.isReceive();
        if(isReceive){
            return IM_RECEIVE;
        }else{
            return IM_SEND;
        }
    }

    @Override
    public int getViewTypeCount() {
        return IM_TYPE_COUNT ;
    }

    private static class ViewHolder {
        ImageView mUserHead;
        TextView mContent;
        TextView mUserName;
        TextView mPostTime;
        boolean isReceive;
    }
}
