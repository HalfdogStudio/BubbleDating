package halfdog.bupt.edu.bubbledating.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import halfdog.bupt.edu.bubbledating.R;

/**
 * Created by andy on 2015/5/10.
 */
public class LeftDrawerListAdapter extends BaseAdapter {
    private Context context;
    private String[] content = {"设置","反馈","关于"};


    public LeftDrawerListAdapter(Context context){
        this.context = context;
    }
    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public Object getItem(int position) {
        return content[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if(convertView == null){
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.item_left_drawer_list,null);
            holder.imageView = (ImageView)convertView.findViewById(R.id.item_left_drawer_list_image);
            holder.textView = (TextView)convertView.findViewById(R.id.item_left_drawer_list_text);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder)convertView.getTag();
        }
        switch (position){
            case 0:
                holder.imageView.setImageDrawable(context.getResources().getDrawable(R.mipmap.self_info));
                holder.textView.setText(context.getResources().getText(R.string.self_info));
                break;
            case 1:
                holder.imageView.setImageDrawable(context.getResources().getDrawable(R.mipmap.feedback));
                holder.textView.setText(context.getResources().getText(R.string.feedback));
                break;
            case 2:
                holder.imageView.setImageDrawable(context.getResources().getDrawable(R.mipmap.info));
                holder.textView.setText(context.getResources().getText(R.string.info));
                break;
        }
        return convertView;
    }

    static class ViewHolder{
        private ImageView imageView;
        private TextView textView;
    }
}
