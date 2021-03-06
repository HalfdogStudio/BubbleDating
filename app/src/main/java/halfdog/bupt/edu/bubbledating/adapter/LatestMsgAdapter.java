package halfdog.bupt.edu.bubbledating.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;

import java.io.File;
import java.util.List;

import halfdog.bupt.edu.bubbledating.R;
import halfdog.bupt.edu.bubbledating.cache.image.ImageCacheManager;
import halfdog.bupt.edu.bubbledating.constants.Configurations;
import halfdog.bupt.edu.bubbledating.entity.ChatMsgEntity;
import halfdog.bupt.edu.bubbledating.tool.DataCache;
import halfdog.bupt.edu.bubbledating.tool.MyDate;

/**
 * Created by andy on 2015/5/6.
 */
public class LatestMsgAdapter extends BaseAdapter {
    public static final String TAG = "LatestMsgAdapter";

    private Context context;
    private List<ChatMsgEntity> list;

    public LatestMsgAdapter(Context context, List<ChatMsgEntity> list){
        this.context = context;
        this.list = list;
    }

    public void refreshAdapter(List<ChatMsgEntity>  list){
        this.list = list;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if(DataCache.mContactUser == null) return 0;
        return DataCache.mContactUser.size();
    }

    @Override
    public Object getItem(int position) {
        return DataCache.mContactUser.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ChatMsgEntity entity = list.get(position);
        ViewHolder holder = null;
        String serverImgPath = null;
        String chatter = null;
        if(entity.isReceive()){
            serverImgPath = Configurations.SERVER_IP+ File.separator+ Configurations.IMG_CACHE_PATH+File.separator+entity.getmFrom()+".png";
            chatter = entity.getmFrom();
        }else{
            serverImgPath = Configurations.SERVER_IP+ File.separator+ Configurations.IMG_CACHE_PATH+File.separator+entity.getTo()+".png";
            chatter = entity.getTo();
        }
//        Configurations.SERVER_IP+ File.separator+Configurations.IMG_CACHE_PATH+File.separator+entity.from()+".png";
//        if(Mode.DEBUG){
//            Log.d(TAG, "-->chat msg adapter of " + entity.getName() + ":" + serverImgPath);
//        }
        if(convertView == null){
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.message_fragment_msg_item,null);
            holder.mUserAvatar = (ImageView)convertView.findViewById(R.id.lattest_msg_avatar);
            holder.mTitle = (TextView)convertView.findViewById(R.id.lattest_msg_title);
            holder.mContent = (TextView)convertView.findViewById(R.id.lattest_msg_content);
            holder.mPosttime = (TextView)convertView.findViewById(R.id.lattest_msg_posttime);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder)convertView.getTag();
        }
        ImageLoader.ImageListener userAvatorListener = ImageLoader.getImageListener( holder.mUserAvatar,
                R.drawable.avatar_default_m, R.drawable.avatar_default_m);
        ImageCacheManager.getInstance().getImage(serverImgPath,userAvatorListener);
//        holder.mUserAvatar.setImageDrawable(context.getResources().getDrawable(R.drawable.avatar_default_m));
        holder.mTitle.setText(chatter);
        holder.mContent.setText(entity.getContent());
        holder.mPosttime.setText(MyDate.diffDate(MyDate.getCurrentDate(),MyDate.parseSimpleDateFormate(entity.getDate())));
        return convertView;
    }

    private  static class ViewHolder{
        ImageView mUserAvatar;
        TextView mTitle;
        TextView mContent;
        TextView mPosttime;
    }
}
