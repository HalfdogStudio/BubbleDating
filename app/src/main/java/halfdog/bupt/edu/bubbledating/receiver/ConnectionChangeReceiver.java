package halfdog.bupt.edu.bubbledating.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

import halfdog.bupt.edu.bubbledating.R;
import halfdog.bupt.edu.bubbledating.tool.NetworkStatusTool;

public class ConnectionChangeReceiver extends BroadcastReceiver {
    private final String TAG = "ConChangeReceiver";
    public ConnectionChangeReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        Log.d(TAG,"-->network status changed");

        if(!NetworkStatusTool.isConnected(context)){
            Toast.makeText(context, R.string.network_state_not_connected,Toast.LENGTH_LONG).show();
            Log.d(TAG,"-->network loss");
        }

    }
}
