package halfdog.bupt.edu.bubbledating.tool;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.text.style.TtsSpan;
import android.util.Log;

import com.baidu.navisdk.util.SysOSAPI;

import java.nio.DoubleBuffer;
import java.security.acl.LastOwnerException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TooManyListenersException;

import halfdog.bupt.edu.bubbledating.BubbleDatingApplication;
import halfdog.bupt.edu.bubbledating.constants.Offline;
import halfdog.bupt.edu.bubbledating.db.MySQLiteOpenHelper;
import halfdog.bupt.edu.bubbledating.entity.ChatMsgEntity;

/**
 * Created by andy on 2015/5/6.
 */
public class DataCache {
    public static final String TAG = "DataCache";
    public static HashMap<String, List<ChatMsgEntity>> mUserMsgList;
    public static List<ChatMsgEntity> mContactUser;
    public static boolean mHasHistoryMsg = true;

    static {
        mContactUser = new ArrayList<>();
        mUserMsgList = new HashMap<>();
    }

    public static void initCacheData(Context context) {
//        mHasHistoryMsg = false;
        if (TextUtils.isEmpty(BubbleDatingApplication.userEntity.getmName())) {
            throw new IllegalStateException("BubbleDatingApplication.userEntity was not initialized.");
        }
        mHasHistoryMsg = true;
        SQLiteDatabase db = MySQLiteOpenHelper.getInstance(context).getWritableDatabase();
        Log.d(TAG,"-->DB:"+db.toString());
        String querySql = "select * from " + MySQLiteOpenHelper.CONTACT_TABLE_NAME + " order by last_contact_date";
        Log.d(TAG,"-->querySql:"+querySql);
//        Cursor cursor = db.rawQuery(querySql, null);
        Cursor cursor = db.query(true,MySQLiteOpenHelper.CONTACT_TABLE_NAME,null,null,null,null,null,null,null);
        List<String> chatterList = new ArrayList<String>();
        while (cursor.moveToNext()) {
            /* mContactUser is just a display of msgs , so isReceive is not so important here  */
            String owner = BubbleDatingApplication.userEntity.getmName();
            String chatter = cursor.getString(1);
            String lastMessage = cursor.getString(2);
            String lastContactDate = cursor.getString(3);
            chatterList.add(chatter);
            mContactUser.add(new ChatMsgEntity(owner,chatter, lastMessage, lastContactDate, true ));
        }
        if (mContactUser.isEmpty() || chatterList.isEmpty()) {
            Log.d(TAG,"-->mContactUser is empty when initialization");
            mHasHistoryMsg = false;
            return;
        }
        for (int i = 0; i < chatterList.size(); i++) {
            Cursor tmpCursor = db.rawQuery("select * from "+MySQLiteOpenHelper.CONTACT_MSG_TABLE_NAME+" where name= ?", new String[]{chatterList.get(i)});
            List<ChatMsgEntity> list = new ArrayList<>();
            while (tmpCursor.moveToNext()) {
                String chatter = tmpCursor.getString(1);
                String date = tmpCursor.getString(2);
                String content = tmpCursor.getString(3);
                boolean isReceive = Boolean.parseBoolean(tmpCursor.getString(4));
                Log.d(TAG,"-->isReceive:"+isReceive);
                String from = null, to = null;
                if(isReceive){
                    from = chatter;
                    to = BubbleDatingApplication.userEntity.getmName();
                }else{
                    from = BubbleDatingApplication.userEntity.getmName();
                    to = chatter;
                }
                ChatMsgEntity entity = new ChatMsgEntity(to,from, content, date, isReceive);
                list.add(entity);
            }
            if (list.isEmpty()) {
                mUserMsgList.put(chatterList.get(i), new ArrayList<ChatMsgEntity>());
            } else {
                mUserMsgList.put(chatterList.get(i), list);
            }

        }

    }

    public static void initOfflineCacheData(Context context) {
        mHasHistoryMsg = true;

        SQLiteDatabase db = MySQLiteOpenHelper.getInstance(context, Offline.OFFLINE_DB).getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from contact_list order by last_contact_date", null);
        while (cursor.moveToNext()) {
            String name = cursor.getString(1);
            String lastMessage = cursor.getString(2);
            String lastContactDate = cursor.getString(3);
            mContactUser.add(new ChatMsgEntity(BubbleDatingApplication.userEntity.getmName(), name, lastMessage, lastContactDate, true));
        }
        if (mContactUser.size() == 0) {
            mHasHistoryMsg = false;
            return;
        }
        for (int i = 0; i < mContactUser.size(); i++) {
            Cursor tmpCursor = db.rawQuery("select * from contact_msg_list where name= ?", new String[]{mContactUser.get(i).getmFrom()});
            List<ChatMsgEntity> list = new ArrayList<>();
            while (tmpCursor.moveToNext()) {
                String name = tmpCursor.getString(1);
                String date = tmpCursor.getString(2);
                String content = tmpCursor.getString(3);
                boolean isReceive = Boolean.parseBoolean(tmpCursor.getString(4));
                String from = null, to = null;
                if(isReceive){
                    from = name;
                    to = BubbleDatingApplication.userEntity.getmName();
                }else{
                    from = BubbleDatingApplication.userEntity.getmName();
                    to = name;
                }
                ChatMsgEntity entity = new ChatMsgEntity(to,from , content, date, isReceive );
                list.add(entity);
            }
            if (list.size() == 0) {
                mUserMsgList.put(mContactUser.get(i).getmFrom(), null);
            } else {
                mUserMsgList.put(mContactUser.get(i).getmFrom(), list);
            }

        }
    }

    public static void updateUsrMsgAndContacListInMemory(ChatMsgEntity entity) {

        boolean isReceive = entity.isReceive();
        String chatter = null;
        if(isReceive){
            chatter = entity.getmFrom();
        }else{
            chatter = entity.getTo();
        }

        if (DataCache.mUserMsgList.containsKey(chatter)) {
            /* update mUserMsgList in memory */
            DataCache.mUserMsgList.get(chatter).add(new ChatMsgEntity(entity.getTo(),entity.getmFrom(), entity.getContent(),
                    entity.getDate(), isReceive ));

            /* update mContactuser in memory */
            int index = 0;
            for (; index < DataCache.mContactUser.size(); index++) {
                Log.d(TAG, "-->index:" + index);
                if (TextUtils.equals(DataCache.mContactUser.get(index).getmFrom(), chatter) ||
                        TextUtils.equals(DataCache.mContactUser.get(index).getTo(), chatter) ) {
                    break;
                }
            }
            Log.d(TAG, "-->AFTER THE FOR LOOP, INDEX:" + index);
            Log.d(TAG,"-->getfrom:"+DataCache.mContactUser.get(index).getmFrom()+",getto:"+DataCache.mContactUser.get(index).getTo()+",chatter:"+chatter);
            if (TextUtils.equals(DataCache.mContactUser.get(index).getmFrom(), chatter) ||
                    TextUtils.equals(DataCache.mContactUser.get(index).getTo(), chatter) ) {
                DataCache.mContactUser.get(index).setTo(entity.getTo());
                DataCache.mContactUser.get(index).setmFrom(entity.getmFrom());
                DataCache.mContactUser.get(index).setContent(entity.getContent());
                DataCache.mContactUser.get(index).setDate(entity.getDate());
            }


        } else {
            List<ChatMsgEntity> list = new ArrayList<>();
            list.add(new ChatMsgEntity(entity.getTo(),entity.getmFrom(), entity.getContent(),
                    entity.getDate(), isReceive));
            DataCache.mUserMsgList.put(chatter, list);

            mContactUser.add(new ChatMsgEntity(entity.getTo(),entity.getmFrom(), entity.getContent(),
                    entity.getDate(), isReceive));
        }

        /*
        *       Important to set mHasHistoryMsg to true, because MessageFragment will set "No message" View or
        *       message View according to this mHasHistoryMsg;
        * */
        setmHasHistoryMsg();


//        if (!mUserMsgList.containsKey(entity.getName())) {
//            mContactUser.add(new ChatMsgEntity(entity.getName(), entity.getContent(),
//                    entity.getDate(), isReceive));
//        } else {
//            int index = 0;
//            for (; index < DataCache.mContactUser.size(); index++) {
//                Log.d(TAG, "-->index:" + index);
//                if (TextUtils.equals(DataCache.mContactUser.get(index).getName(), entity.getName())) {
//                    break;
//                }
//            }
//            Log.d(TAG, "-->AFTER THE FOR LOOP, INDEX:" + index);
//            if (TextUtils.equals(DataCache.mContactUser.get(index).getName(), entity.getName())) {
//
//                DataCache.mContactUser.get(index).setContent(entity.getContent());
//                DataCache.mContactUser.get(index).setDate(entity.getDate());
//            }
//        }
    }

    public static void updateUsrMsgAndContactListInDB(ChatMsgEntity entity, SQLiteDatabase db, Context context) throws SQLException {
        db = MySQLiteOpenHelper.getInstance(context).getWritableDatabase();
        boolean isReceive = entity.isReceive();
        String chatter = isReceive?entity.getmFrom():entity.getTo();
        Log.d(TAG, "-->DB , CHATTER IS:" + chatter);
            /* insert into contact_msg_list db*/
//        String insertSql = "insert into " + MySQLiteOpenHelper.CONTACT_MSG_TABLE_NAME +
//                " (name,date,content,is_receive) values ('" + chatter + "','" + entity.getDate() +
//                "','" + entity.getContent() + "','" + isReceive + "')";
//        Log.d(TAG, "-->INSERT SQL:" + insertSql);
        ContentValues values = new ContentValues();
        values.put("name",chatter);
        values.put("date",entity.getDate());
        values.put("content",entity.getContent());
        Log.d(TAG,"-->save to sqlite: isReceive:"+ String.valueOf(isReceive));
        values.put("is_receive", String.valueOf(isReceive));
        long res = db.insert(MySQLiteOpenHelper.CONTACT_MSG_TABLE_NAME,null,values);
        Log.d(TAG,"-->res of insert into chat record:"+res);

            /* update contact_list db 最近的消息， message fragment */
            /*
            *       消息更新的逻辑是 ：
            *       先更新内存消息，再更新db消息，所以这里不能使用内存变量来判断消息是否缓存到数据库中，
            *       因为结果将总是为“是”
            * */
        String query = "select * from "+MySQLiteOpenHelper.CONTACT_TABLE_NAME +" where name = ?";
        Cursor cursor = db.rawQuery(query,new String[]{chatter});
        if(!cursor.moveToFirst()){
            /*cursor is not empty*/
//            String insertSql2 = "insert into " + MySQLiteOpenHelper.CONTACT_TABLE_NAME +
//                    "(name,last_message,last_contact_date) values ('" +
//                    chatter + "','" + entity.getContent() + "','" + entity.getDate() + "')";
//            Log.d(TAG, "--> insert sql 2: " + insertSql2);
            ContentValues values1 = new ContentValues();
            values1.put("name",chatter);
            values1.put("last_message", entity.getContent());
            values1.put("last_contact_date", entity.getDate());
            long res2 = db.insert(MySQLiteOpenHelper.CONTACT_TABLE_NAME,null,values1);
            Log.d(TAG,"-->res of insert into latest msg:"+res2);
        }else{
            /*cursor is empty*/
            String updateSql = "update " + MySQLiteOpenHelper.CONTACT_TABLE_NAME + " set last_message='" +
                    entity.getContent() + "',last_contact_date='" + entity.getDate() + "' where name='"+chatter+"'" ;
            Log.d(TAG, "-->updateSql:" + updateSql);
            db.execSQL(updateSql);
            ContentValues values2 = new ContentValues();
            values2.put("last_message",entity.getContent());
            values2.put("last_contact_date", entity.getDate());
            int res2 = db.update(MySQLiteOpenHelper.CONTACT_TABLE_NAME,values2,"name='"+chatter+"'",null);
            Log.d(TAG,"-->number of rows updated in latest msg:"+res2);
        }
//        cursor.close();


    }
    public static void setmHasHistoryMsg() {
        mHasHistoryMsg = true;
    }

    public static void showLatestMsgAndChatRecord(){
        Log.d(TAG,"-->display latest msg and chat record");
        if(mContactUser.isEmpty()){
            Log.d(TAG,"-->mContactUser is empty");
        }else{
            for(ChatMsgEntity entity : mContactUser){
                Log.d(TAG,"--> entity:"+entity.toString());
            }
        }
        if(mUserMsgList.isEmpty()){
            Log.d(TAG,"-->mUserMsgList is empty");
        }else{
            for(String key : mUserMsgList.keySet()){
                List<ChatMsgEntity> list = mUserMsgList.get(key);
                Log.d(TAG,"-->USER NAME : "+ key);
                    if(!list.isEmpty()){
                        for(ChatMsgEntity entity : list){
                            Log.d(TAG,"--> " + entity.toString());
                        }
                    }
            }
        }

    }

}
