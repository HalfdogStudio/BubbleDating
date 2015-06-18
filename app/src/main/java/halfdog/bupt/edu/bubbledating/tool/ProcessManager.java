package halfdog.bupt.edu.bubbledating.tool;

import android.app.ActivityManager;
import android.content.Context;
import android.nfc.Tag;
import android.util.Log;

import java.util.List;

/**
 * Created by andy on 2015/6/10.
 */
public class ProcessManager {
    private static final String TAG = "ProcessManager";

    public static boolean isBackGround(Context context){
        ActivityManager activityManager = (ActivityManager)
                context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcesses =
                activityManager.getRunningAppProcesses();
        for(ActivityManager.RunningAppProcessInfo processInfo:appProcesses){
            if(processInfo.processName.equals(context.getPackageName())){
                if(processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_BACKGROUND){
                    Log.i(TAG,"-->Background App:"+processInfo.processName);
                    return true;
                }else{
                    Log.i(TAG,"-->Foreground App:"+processInfo.processName);
                    return false;
                }
            }
        }
        return false;
    }
}
