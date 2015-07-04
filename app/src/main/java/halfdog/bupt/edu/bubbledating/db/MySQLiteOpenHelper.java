package halfdog.bupt.edu.bubbledating.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by andy on 2015/5/5.
 */
public class MySQLiteOpenHelper extends SQLiteOpenHelper {

    private static final int VERSION_NUMBER = 1;
    /*   DB_NAME should be BubbleDatingApplication.userEntity.getmName() + ".db"  */
    private static  String ON_LINE_DB_NAME = null;
    private static final String OFF_LINE_DB_NAME = "bubble_dating_offline.db";
//    private static final String OFF_LINE_DB_NAME = "bubble_dating_offline.db";

    private static MySQLiteOpenHelper instance;

    public static  final String CONTACT_TABLE_NAME = "latest_msg";
    public static  final String CONTACT_MSG_TABLE_NAME = "chat_record";
    private final String CREATE_COTACT_LIST = " create table if not exists "+CONTACT_TABLE_NAME +
            " (_id integer primary key autoincrement, name,last_message, last_contact_date) " ;
    private final String CREATE_MSG_LIST = "create table if not exists "+CONTACT_MSG_TABLE_NAME +
            " (_id integer primary key autoincrement, name, date, content, is_receive) ";




    public MySQLiteOpenHelper(Context context,String dbName){
        super(context,dbName,null,VERSION_NUMBER);
    }

    public MySQLiteOpenHelper(Context context){
        super(context,OFF_LINE_DB_NAME,null,VERSION_NUMBER);
    }

    public static MySQLiteOpenHelper getInstance(Context context,String dbName){
        if(instance == null){
            instance = new MySQLiteOpenHelper(context,dbName);
        }
        return instance;
    }

    public static MySQLiteOpenHelper getInstance(Context context){
        if(instance == null){
            instance = new MySQLiteOpenHelper(context);
        }
        return instance;
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
//        db.execSQL("drop table if exists "+CONTACT_TABLE_NAME);
//        db.execSQL("drop table if exists "+CONTACT_MSG_TABLE_NAME);
        db.execSQL(CREATE_COTACT_LIST);
        db.execSQL(CREATE_MSG_LIST);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public static boolean isTableExists(String tableName) {
        SQLiteDatabase db = instance.getReadableDatabase();

        Cursor cursor = db.rawQuery("select * from contact_list", null);
        if(cursor!=null) {
            if(cursor.getCount()>0) {
                cursor.close();
                return true;
            }
            cursor.close();
        }
        return false;
    }

    public void close(){
        if(instance != null){
            try{
                SQLiteDatabase db = instance.getWritableDatabase();
                db.close();
            }catch(Exception e){
                e.printStackTrace();
            }

            instance = null;
        }
    }


}
