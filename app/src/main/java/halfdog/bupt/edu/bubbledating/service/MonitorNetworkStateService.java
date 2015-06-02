package halfdog.bupt.edu.bubbledating.service;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

import halfdog.bupt.edu.bubbledating.receiver.ConnectionChangeReceiver;

public class MonitorNetworkStateService extends Service {

    private final String TAG = "NetworkStateService";
    private static final String ACTION1 = "android.net.conn.CONNECTIVITY_CHANGE";
    private static final String ACTION2 = "android.intent.action.USER_PRESENT";
    private ConnectionChangeReceiver receiver = new ConnectionChangeReceiver();

    public MonitorNetworkStateService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
//        ConnectionChangeReceiver receiver = new ConnectionChangeReceiver();
        Log.d(TAG, "-->start Monitor Network State service, register receiver");
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION1);
        filter.addAction(ACTION2);
        this.registerReceiver(receiver,filter);


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.unregisterReceiver(receiver);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
