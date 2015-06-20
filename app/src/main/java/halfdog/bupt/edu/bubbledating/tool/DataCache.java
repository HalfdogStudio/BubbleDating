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
            String name = cursor.getString(1);
            String lastMessage = cursor.getString(2);
            String lastContactDate = cursor.getString(3);
            mContactUser.add(new ChatMsgEntity(name, lastMessage, lastContactDate, true));
        }
        if (mContactUser.size() == 0) {
            mHasHistoryMsg = false;
            return;
        }
        for (int i = 0; i < mContactUser.size(); i++) {
            Cursor tmpCursor = db.rawQuery("select * from contact_msg_list where name= ?", new String[]{mContactUser.get(i).getName()});
            List<ChatMsgEntity> list = new ArrayList<>();
            while (tmpCursor.moveToNext()) {
                String name = tmpCursor.getString(1);
                String date = tmpCursor.getString(2);
                String content = tmpCursor.getString(3);
                boolean isReceive = Boolean.parseBoolean(tmpCursor.getString(4));
                ChatMsgEntity entity = new ChatMsgEntity(name, content, date, isReceive);
                list.add(entity);
            }
            if (list.size() == 0) {
                mUserMsgList.put(mContactUser.get(i).getName(), null);
            } else {
                mUserMsgList.put(mContactUser.get(i).getName(), list);
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
            mContactUser.add(new ChatMsgEntity(name, lastMessage, lastContactDate, true));
        }
        if (mContactUser.size() == 0) {
            mHasHistoryMsg = false;
            return;
        }
        for (int i = 0; i < mContactUser.size(); i++) {
            Cursor tmpCursor = db.rawQuery("select * from contact_msg_list where name= ?", new String[]{mContactUser.get(i).getName()});
            List<ChatMsgEntity> list = new ArrayList<>();
            while (tmpCursor.moveToNext()) {
                String name = tmpCursor.getString(1);
                String date = tmpCursor.getString(2);
                String content = tmpCursor.getString(3);
                boolean isReceive = Boolean.parseBoolean(tmpCursor.getString(4));
                ChatMsgEntity entity = new ChatMsgEntity(name, content, date, isReceive);
                list.add(entity);
            }
            if (list.size() == 0) {
                mUserMsgList.put(mContactUser.get(i).getName(), null);
            } else {
                mUserMsgList.put(mContactUser.get(i).getName(), list);
            }

        }
    }


    public static void updateUseMsgListAndContactUser(ChatMsgEntity entity, SQLiteDatabase db, boolean isReceive) {
        {
            /* insert into contact_msg_list */
            String insertSql = "insert into " + MySQLiteOpenHelper.CONTACT_MSG_TABLE_NAME +
                    " (name,date,content,is_receive) values ('" + entity.getName() + "','" + entity.getDate() +
                    "','" + entity.getContent() + "','" + false + "')";
            Log.d(TAG, "-->INSERT SQL:" + insertSql);
            db.execSQL(insertSql);


            if (DataCache.mUserMsgList.containsKey(entity.getName())) {
                DataCache.mUserMsgList.get(entity.getName()).add(new ChatMsgEntity(entity.getName(), entity.getContent(),
                        entity.getDate(), isReceive));
            } else {
                List<ChatMsgEntity> list = new ArrayList<>();
                list.add(new ChatMsgEntity(entity.getName(), entity.getContent(),
                        entity.getDate(), isReceive));
                DataCache.mUserMsgList.put(entity.getName(), list);
            }

            /* update contact_list 最近的消息， message fragment */
            String querySql = "select * from " + MySQLiteOpenHelper.CONTACT_TABLE_NAME +
                    " where name='" + entity.getName() + "'";
            Log.d(TAG, "-->query 1:" + querySql);
            Cursor cursor = db.rawQuery(querySql, null);
            if (cursor.getCount() == 0) {
                String insertSql2 = "insert into " + MySQLiteOpenHelper.CONTACT_TABLE_NAME +
                        "(name,last_message,last_contact_date) values ('" +
                        entity.getName() + "','" + entity.getContent() + "','" + entity.getDate() + "')";
                Log.d(TAG, "--> insert sql 2: " + insertSql2);
                db.execSQL(insertSql2);
                cursor.close();

                DataCache.mContactUser.add(new ChatMsgEntity(entity.getName(), entity.getContent(),
                        entity.getDate(), isReceive));
            } else {
                String updateSql = "update " + MySQLiteOpenHelper.CONTACT_TABLE_NAME + " set last_message='" +
                        entity.getContent() + "',last_contact_date='" + entity.getDate() + "'";
                Log.d(TAG, "-->updateSql:" + updateSql);
                db.execSQL(updateSql);

                int index = 0;
                for (; index < DataCache.mContactUser.size(); index++) {
                    Log.d(TAG, "-->index:" + index);
                    if (TextUtils.equals(DataCache.mContactUser.get(index).getName(), entity.getName())) {
                        break;
                    }
                }
                Log.d(TAG, "-->AFTER THE FOR LOOP, INDEX:" + index);
                if (TextUtils.equals(DataCache.mContactUser.get(index).getName(), entity.getName())) {

                    DataCache.mContactUser.get(index).setContent(entity.getContent());
                    DataCache.mContactUser.get(index).setDate(entity.getDate());
                }
            }
        }
    }

}
