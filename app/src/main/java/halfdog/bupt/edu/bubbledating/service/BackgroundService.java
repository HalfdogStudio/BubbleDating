package halfdog.bupt.edu.bubbledating.service;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.sqlite.SQLiteDatabase;
import android.os.IBinder;
import android.util.Log;

import com.easemob.EMEventListener;
import com.easemob.EMNotifierEvent;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMMessage;

import halfdog.bupt.edu.bubbledating.BubbleDatingApplication;
import halfdog.bupt.edu.bubbledating.db.MySQLiteOpenHelper;
import halfdog.bupt.edu.bubbledating.receiver.ConnectionChangeReceiver;
import halfdog.bupt.edu.bubbledating.tool.HXTool.HXNotifier;
import halfdog.bupt.edu.bubbledating.tool.HXTool.HXSDKHelper;

public class BackgroundService extends Service implements EMEventListener {

    /*
    *       This service is to deal with network state change and listen for new msg.
    * */

    private final String TAG = "NetworkStateService";
    private static final String ACTION1 = "android.net.conn.CONNECTIVITY_CHANGE";
    private static final String ACTION2 = "android.intent.action.USER_PRESENT";
    private ConnectionChangeReceiver receiver = new ConnectionChangeReceiver();

    public SQLiteDatabase db;

    public BackgroundService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
//        ConnectionChangeReceiver receiver = new ConnectionChangeReceiver();
        Log.d(TAG, "-->start Monitor Network State service, register receiver");
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION1);
        filter.addAction(ACTION2);
        this.registerReceiver(receiver, filter);
        EMChatManager.getInstance().registerEventListener(this);

        db = MySQLiteOpenHelper.getInstance(this).getWritableDatabase();


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.unregisterReceiver(receiver);
        EMChatManager.getInstance().unregisterEventListener(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onEvent(EMNotifierEvent emNotifierEvent) {
        {
            {
                Log.d(TAG,"-->EMNotifierEvent:"+emNotifierEvent.getData().toString());
                Log.d(TAG,"-->EMNotifierEvent:"+emNotifierEvent.getEvent().toString());

                switch (emNotifierEvent.getEvent()) {
                    case EventNewMessage: //普通消息
                    {
                        EMMessage message = (EMMessage) emNotifierEvent.getData();
                        Log.d(TAG,"-->normal message:"+message.toString());
                        //提示新消息
                        HXSDKHelper instance = HXSDKHelper.getInstance();
                        HXNotifier notifiier =  instance.getNotifier();
                        notifiier.onNewMsg(message,this,db);

//                    refreshUI();
                        break;
                    }

                    case EventOfflineMessage:
                    {
                        Log.d(TAG,"-->get offline message");
//                    refreshUI();
                        break;
                    }

                    default:
                        Log.d(TAG,"-->get default message");
                        break;
                }
            }
        }
    }
}
