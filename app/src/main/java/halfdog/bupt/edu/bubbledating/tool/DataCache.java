package halfdog.bupt.edu.bubbledating.tool;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;

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
        MySQLiteOpenHelper helper = MySQLiteOpenHelper.getInstance(context, BubbleDatingApplication.userEntity.getmName());
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from contact_list order by last_contact_date", null);
        while (cursor.moveToNext()) {
            String to = BubbleDatingApplication.userEntity.getmName();
            String from = cursor.getString(1);
            String lastMessage = cursor.getString(2);
            String lastContactDate = cursor.getString(3);
            mContactUser.add(new ChatMsgEntity(to,from, lastMessage, lastContactDate, true ));
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
                ChatMsgEntity entity = new ChatMsgEntity(to,from, content, date, isReceive);
                list.add(entity);
            }
            if (list.size() == 0) {
                mUserMsgList.put(mContactUser.get(i).getmFrom(), null);
            } else {
                mUserMsgList.put(mContactUser.get(i).getmFrom(), list);
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

    public static void updateUsrMsgAndContactListInDB(ChatMsgEntity entity, SQLiteDatabase db) throws SQLException {
        boolean isReceive = entity.isReceive();
            /* insert into contact_msg_list db*/
        String insertSql = "insert into " + MySQLiteOpenHelper.CONTACT_MSG_TABLE_NAME +
                " (name,date,content,is_receive) values ('" + entity.getTo() + "','" + entity.getDate() +
                "','" + entity.getContent() + "','" + isReceive + "')";
        Log.d(TAG, "-->INSERT SQL:" + insertSql);
        db.execSQL(insertSql);

            /* update contact_list db 最近的消息， message fragment */
        if (!mUserMsgList.containsKey(entity.getTo())) {
            String insertSql2 = "insert into " + MySQLiteOpenHelper.CONTACT_TABLE_NAME +
                    "(name,last_message,last_contact_date) values ('" +
                    entity.getTo() + "','" + entity.getContent() + "','" + entity.getDate() + "')";
            Log.d(TAG, "--> insert sql 2: " + insertSql2);
            db.execSQL(insertSql2);
        } else {
            String updateSql = "update " + MySQLiteOpenHelper.CONTACT_TABLE_NAME + " set last_message='" +
                    entity.getContent() + "',last_contact_date='" + entity.getDate() + "'";
            Log.d(TAG, "-->updateSql:" + updateSql);
            db.execSQL(updateSql);
        }
    }





    public static void setmHasHistoryMsg() {
        mHasHistoryMsg = true;
    }

}
