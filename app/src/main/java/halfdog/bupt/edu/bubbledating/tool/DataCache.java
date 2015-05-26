package halfdog.bupt.edu.bubbledating.tool;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import halfdog.bupt.edu.bubbledating.BubbleDatingApplication;
import halfdog.bupt.edu.bubbledating.constants.Offline;
import halfdog.bupt.edu.bubbledating.db.MySQLiteOpenHelper;
import halfdog.bupt.edu.bubbledating.entity.ChatMsgEntity;

/**
 * Created by andy on 2015/5/6.
 */
public class DataCache {
    public static final String TAG = "DataCache";
    public static HashMap<String,List<ChatMsgEntity>> mUserMsgList;
    public static List<ChatMsgEntity> mContactUser;
    public static boolean mHasHistoryMsg = true;
    static {
        mContactUser = new ArrayList<ChatMsgEntity>();
        mUserMsgList = new HashMap<String,List<ChatMsgEntity>>();
    }

    public static void initCacheData(Context context){
//        mHasHistoryMsg = false;
        if(TextUtils.isEmpty(BubbleDatingApplication.userEntity.getmName())){
            throw new IllegalStateException("BubbleDatingApplication.userEntity was not initialized.");
        }
        mHasHistoryMsg = true;
        MySQLiteOpenHelper helper = MySQLiteOpenHelper.getInstance(context, BubbleDatingApplication.userEntity.getmName());
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from contact_list order by last_contact_date",null);
        while(cursor.moveToNext()){
            String name = cursor.getString(1);
            String lastMessage = cursor.getString(2);
            String lastContactDate = cursor.getString(3);
            mContactUser.add(new ChatMsgEntity(name,lastMessage,lastContactDate,true));
        }
        if(mContactUser.size() == 0){
            mHasHistoryMsg = false;
            return ;
        }
        for(int i = 0; i < mContactUser.size(); i ++ ){
            Cursor tmpCursor = db.rawQuery("select * from contact_msg_list where name= ?",new String[]{mContactUser.get(i).getName()});
            List<ChatMsgEntity> list = new ArrayList<ChatMsgEntity>();
            while(tmpCursor.moveToNext()){
                String name = tmpCursor.getString(1);
                String date = tmpCursor.getString(2);
                String content = tmpCursor.getString(3);
                boolean isReceive = Boolean.parseBoolean(tmpCursor.getString(4));
                ChatMsgEntity entity = new ChatMsgEntity(name,content,date,isReceive);
                list.add(entity);
//                Log.d(TAG,"-->chat msg entity:"+entity.toString());
            }
            if(list.size() == 0){
                mUserMsgList.put(mContactUser.get(i).getName(),null);
            }else{
                mUserMsgList.put(mContactUser.get(i).getName(),list);
            }

        }
        Log.d(TAG,"-->导入缓存数据成功");

    }
    public static void initOfflineCacheData(Context context){
//        Log.d(TAG,"-->contact_list EXISTS?:"+MySQLiteOpenHelper.isTableExists("contact_list"));
//        Log.d(TAG,"-->contact_msg_list EXISTS?:"+MySQLiteOpenHelper.isTableExists("contact_msg_list"));
        mHasHistoryMsg = true;

        SQLiteDatabase db = MySQLiteOpenHelper.getInstance(context, Offline.OFFLINE_DB).getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from contact_list order by last_contact_date",null);
        while(cursor.moveToNext()){
            String name = cursor.getString(1);
            String lastMessage = cursor.getString(2);
            String lastContactDate = cursor.getString(3);
            mContactUser.add(new ChatMsgEntity(name,lastMessage,lastContactDate,true));
        }
        if(mContactUser.size() == 0){
            mHasHistoryMsg = false;
            return ;
        }
        for(int i = 0; i < mContactUser.size(); i ++ ){
            Cursor tmpCursor = db.rawQuery("select * from contact_msg_list where name= ?",new String[]{mContactUser.get(i).getName()});
            List<ChatMsgEntity> list = new ArrayList<ChatMsgEntity>();
            while(tmpCursor.moveToNext()){
                String name = tmpCursor.getString(1);
                String date = tmpCursor.getString(2);
                String content = tmpCursor.getString(3);
                boolean isReceive = Boolean.parseBoolean(tmpCursor.getString(4));
                ChatMsgEntity entity = new ChatMsgEntity(name,content,date,isReceive);
                list.add(entity);
//                Log.d(TAG,"-->chat msg entity:"+entity.toString());
            }
            if(list.size() == 0){
                mUserMsgList.put(mContactUser.get(i).getName(),null);
            }else{
                mUserMsgList.put(mContactUser.get(i).getName(),list);
            }

        }
        Log.d(TAG, "-->导入离线数据成功");
    }
}
