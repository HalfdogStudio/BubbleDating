package halfdog.bupt.edu.bubbledating.tool.HXTool;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;

import com.easemob.EMCallBack;
import com.easemob.EMConnectionListener;
import com.easemob.EMError;
import com.easemob.chat.EMChat;
import com.easemob.chat.EMChatConfig;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMChatOptions;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by andy on 2015/5/28.
 */
public  class HXSDKHelper {

    private static final String TAG = "HXSDKHelper";
    /**
     * application context
     */
    public  Context appContext = null;

    /**
     * HuanXin mode helper, which will manage the user data and user preferences
     */
    public  HXSDKModel hxModel = null;

    /**
     * MyConnectionListener
     */
    public EMConnectionListener connectionListener = null;

    /**
     * HuanXin ID in cache
     */
    public String hxId = null;

    /**
     * password in cache
     */
    public String password = null;

    /**
     * init flag: test if the sdk has been inited before, we don't need to init again
     */
    private boolean sdkInited = false;

    /**
     * the global HXSDKHelper instance
     */
    private static HXSDKHelper instance =  new HXSDKHelper();;

    /**
     * the notifier
     */
    public HXNotifier notifier = null;

    private HXSDKHelper(){
    }

    public static HXSDKHelper getInstance(){
        return instance;
    }

    /**
     * to record foreground Activity
     */
    private List<Activity> activityList = new ArrayList<Activity>();

    public void pushActivity(Activity activity){
        if(!activityList.contains(activity)){
            activityList.add(0,activity);
        }
    }

    public void popActivity(Activity activity){
        activityList.remove(activity);
    }

    /**
     * this function will initialize the HuanXin SDK
     *
     * @return boolean true if caller can continue to call HuanXin related APIs after calling onInit, otherwise false.
     *
     *
     * public class DemoHXSDKHelper extends HXSDKHelper
     *
     * HXHelper = new DemoHXSDKHelper();
     * if(HXHelper.onInit(context)){
     *     // do HuanXin related work
     * }
     */
    public synchronized boolean onInit(Context context){
//        if(sdkInited){
//            return true;
//        }
        Log.d(TAG,"-->hx sdk helper init.");

        appContext = context;

        // create HX SDK model
        hxModel = createModel();

        // create a defalut HX SDK model in case subclass did not provide the model
//        if(hxModel == null){
//            hxModel = new DefaultHXSDKModel(appContext);
//        }

//        int pid = android.os.Process.myPid();
//        String processAppName = getAppName(pid);
//
//        Log.d(TAG, "-->process app name : " + processAppName);


//        if(hxModel.isSandboxMode()){
//            EMChat.getInstance().setEnv(EMChatConfig.EMEnvMode.EMSandboxMode);
//        }

//        if(hxModel.isDebugMode()){
//            // set debug mode in development process
//            EMChat.getInstance().setDebugMode(true);
//        }

        Log.d(TAG, "-->initialize EMChat SDK");

        initHXOptions();
        initListener();
//        sdkInited = true;
        return true;
    }


    public HXSDKModel getModel(){
        return hxModel;
    }

    public String getHXId(){
        if(hxId == null){
            hxId = hxModel.getHXId();
        }
        return hxId;
    }

    public String getPassword(){
        if(password == null){
            password = hxModel.getPwd();
        }
        return password;
    }

    public void setHXId(String hxId){
        if (hxId != null) {
            if(hxModel.saveHXId(hxId)){
                this.hxId = hxId;
            }
        }
    }

    public void setPassword(String password){
        if(hxModel.savePassword(password)){
            this.password = password;
        }
    }

    /**
     * the subclass must override this class to provide its own model or directly use {@link DefaultHXSDKModel}
     * @return
     */
     public  HXSDKModel createModel(){
         return new DefaultHXSDKModel(appContext) {
         };
     };

    /**
     * please make sure you have to get EMChatOptions by following method and set related options
     *      EMChatOptions options = EMChatManager.getInstance().getChatOptions();
     */
    public void initHXOptions(){
        Log.d(TAG, "-->init HuanXin Options");


        notifier = new HXNotifier();
        notifier.init(appContext);

        notifier.setNotificationInfoProvider(getNotificationListener());
    }

    /**
     * subclass can override this api to return the customer notifier
     *
     * @return
     */
    public HXNotifier createNotifier(){
        return new HXNotifier();
    }

    public HXNotifier getNotifier(){
        return notifier;
    }

    /**
     * logout HuanXin SDK
     */
    public void logout(final EMCallBack callback){
        setPassword(null);
        EMChatManager.getInstance().logout(new EMCallBack(){

            @Override
            public void onSuccess() {
                // TODO Auto-generated method stub
                if(callback != null){
                    callback.onSuccess();
                }
            }

            @Override
            public void onError(int code, String message) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onProgress(int progress, String status) {
                // TODO Auto-generated method stub
                if(callback != null){
                    callback.onProgress(progress, status);
                }
            }

        });
    }

    /**
     * @return
     */
    public boolean isLogined(){
        return EMChat.getInstance().isLoggedIn();
    }

    public HXNotifier.HXNotificationInfoProvider getNotificationListener(){
        return null;
    }

    /**
     * init HuanXin listeners
     */
    public void initListener(){
        Log.d(TAG, "-->init listener");

        // create the global connection listener
        connectionListener = new EMConnectionListener() {
            @Override
            public void onDisconnected(int error) {
                if (error == EMError.USER_REMOVED) {
                    onCurrentAccountRemoved();
                }else if (error == EMError.CONNECTION_CONFLICT) {
                    onConnectionConflict();
                }else{
                    onConnectionDisconnected(error);
                }
            }

            @Override
            public void onConnected() {
                onConnectionConnected();
            }
        };

        EMChatManager.getInstance().addConnectionListener(connectionListener);
    }

    /**
     * the developer can override this function to handle connection conflict error
     */
    protected void onConnectionConflict(){}


    /**
     * the developer can override this function to handle user is removed error
     */
    protected void onCurrentAccountRemoved(){}


    /**
     * handle the connection connected
     */
    protected void onConnectionConnected(){}

    /**
     * handle the connection disconnect
     * @param error see {@link EMError}
     */
    protected void onConnectionDisconnected(int error){}

    /**
     * check the application process name if process name is not qualified, then we think it is a service process and we will not init SDK
     * @param pID
     * @return
     */
    private String getAppName(int pID) {
        String processName = null;
        ActivityManager am = (ActivityManager) appContext.getSystemService(Context.ACTIVITY_SERVICE);
        List l = am.getRunningAppProcesses();
        Iterator i = l.iterator();
        PackageManager pm = appContext.getPackageManager();
        while (i.hasNext()) {
            ActivityManager.RunningAppProcessInfo info = (ActivityManager.RunningAppProcessInfo) (i.next());
            try {
                if (info.pid == pID) {
                    CharSequence c = pm.getApplicationLabel(pm.getApplicationInfo(info.processName, PackageManager.GET_META_DATA));
                    // Log.d("Process", "Id: "+ info.pid +" ProcessName: "+
                    // info.processName +"  Label: "+c.toString());
                    // processName = c.toString();
                    processName = info.processName;
                    return processName;
                }
            } catch (Exception e) {
                // Log.d("Process", "Error>> :"+ e.toString());
            }
        }
        return processName;
    }
}
