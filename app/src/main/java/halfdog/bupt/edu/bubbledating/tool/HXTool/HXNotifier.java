package halfdog.bupt.edu.bubbledating.tool.HXTool;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Message;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMMessage;
import com.easemob.util.EMLog;

import java.util.HashSet;

import halfdog.bupt.edu.bubbledating.BubbleDatingApplication;
import halfdog.bupt.edu.bubbledating.R;
import halfdog.bupt.edu.bubbledating.activity.ChatActivity;
import halfdog.bupt.edu.bubbledating.activity.MainActivity;
import halfdog.bupt.edu.bubbledating.constants.Configurations;
import halfdog.bupt.edu.bubbledating.entity.ChatMsgEntity;
import halfdog.bupt.edu.bubbledating.fragment.dummy.MessageFragment;
import halfdog.bupt.edu.bubbledating.tool.DataCache;
import halfdog.bupt.edu.bubbledating.tool.MyDate;
import halfdog.bupt.edu.bubbledating.tool.ProcessManager;

/**
 *
 * this class is subject to be inherited and implement the relative APIs
 */
public class HXNotifier {
    private final static String TAG = "notify";

    Ringtone ringtone = null;


    public static int notifyID = 0525; // start notification id
    public static int foregroundNotifyID = 0555;



    public HashSet<String> fromUsers = new HashSet<String>();
    public int notificationNum = 0;


    public String packageName;
    public String[] msgs;
    public long lastNotifiyTime;
    public AudioManager audioManager;
    public Vibrator vibrator;
    public HXNotificationInfoProvider notificationInfoProvider;

    public static Context appContext;
    public static NotificationManager notificationManager = null;
    public static HXNotifier instance = new HXNotifier();



    public HXNotifier() {
    }

    /**
     * this function can be override
     * @param context
     * @return
     */
    public HXNotifier init(Context context){
        appContext = context;
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Log.d(TAG,"-->init NotificationManager in HXNotifier");

        packageName = appContext.getApplicationInfo().packageName;
//        if (Locale.getDefault().getLanguage().equals("zh")) {
//            msgs = msg_ch;
//        } else {
//            msgs = msg_eng;
//        }

        audioManager = (AudioManager) appContext.getSystemService(Context.AUDIO_SERVICE);
        vibrator = (Vibrator) appContext.getSystemService(Context.VIBRATOR_SERVICE);

        return this;
    }

    /**
     * this function can be override
     */
    public void reset(){
        resetNotificationCount();
        cancelNotificaton();
    }

    void resetNotificationCount() {
        notificationNum = 0;
        fromUsers.clear();
    }

    void cancelNotificaton() {
        if (notificationManager != null)
            notificationManager.cancel(notifyID);
    }

    /*
    *       return the single instance of HXNotifier
    * */
    public static HXNotifier getInstance(){
        return instance;
    }

    /**
     * this function can be override
     *
     * @param message
     */
    public synchronized void onNewMsg(final EMMessage message,Context context,SQLiteDatabase db,String messageContent) {
//        if(EMChatManager.getInstance().isSlientMessage(message)){
//            return;
//        }
//        Log.d(TAG,"-->on New Msg in HX Notifier");
        if(ProcessManager.isBackGround(context)){
            Log.d(TAG, "--> process is in background");
            sendNotification(message, false, messageContent);
            ChatMsgEntity entity = new ChatMsgEntity(message.getTo(),message.getFrom(),
                    messageContent,MyDate.getCurSimpleDateFormate().toString(),true);

            DataCache.updateUsrMsgAndContacListInMemory(entity);

            ChatActivity.sendOrReceiveUiMsg(entity, true);
            DataCache.updateUsrMsgAndContactListInDB(entity, db,context);
        }else{
            Log.d(TAG, "--> app is in foreground");
//            sendNotification(message, true, messageContent);
            ChatMsgEntity entity = new ChatMsgEntity(message.getTo(),message.getFrom(),
                    messageContent,MyDate.getCurSimpleDateFormate().toString(),true);

            DataCache.updateUsrMsgAndContacListInMemory(entity);



            Log.d(TAG, "-->current activity:" + BubbleDatingApplication.getCurrentActivity());
            if(BubbleDatingApplication.getCurrentActivity() instanceof  ChatActivity && TextUtils.equals(message.getFrom(),ChatActivity.chatter)){
                Message msg = ChatActivity.mHandler.obtainMessage(Configurations.UPDATE_CHAT_ACTIVITY_CONTACT);
                msg.obj = entity;
                ChatActivity.mHandler.sendMessage(msg);
            }else if (BubbleDatingApplication.getCurrentActivity() instanceof MainActivity && MainActivity.currentFragment instanceof MessageFragment){
                Log.d(TAG,"--> refresh message fragment.");
                Message msg = MessageFragment.mhandler.obtainMessage(Configurations.UPDATE_MESSAGE_FRAGMENT,entity);
                MessageFragment.mhandler.sendMessage(msg);
            }
            DataCache.updateUsrMsgAndContactListInDB(entity, db,context);
        }
//        if (!EasyUtils.isAppRunningForeground(appContext)) {
//            Log.d(TAG, "-->app is running in backgroud");
//            sendNotification(message, false);
//        } else {
//            Log.d(TAG, "-->app is running in foreground");
//            sendNotification(message, true);
//
//        }
        Log.d(TAG,"--> vibrate and play tone");
        viberateAndPlayTone(message);
        Toast.makeText(appContext,"receive message from:"+message.getFrom()+","+message.toString(),Toast.LENGTH_LONG).show();
    }

    /**
     * This can be override by subclass to provide customer implementation
     * @param message
     */
    protected void sendNotification(EMMessage message, boolean isForeground,String messageContent) {
        Log.d(TAG,"-->send Notification:"+message.toString()+",isForeground:"+isForeground);
        String username = message.getFrom();
        try {
            String notifyText = "";
            switch (message.getType()) {
                case TXT:
                    notifyText += messageContent;
                    break;
                case IMAGE:
                    break;
                case VOICE:
                    break;
                case LOCATION:
                    break;
                case VIDEO:
                    break;
                case FILE:
                    break;
            }

//            PackageManager packageManager = appContext.getPackageManager();
//            String appname = (String) packageManager.getApplicationLabel(appContext.getApplicationInfo());

            // notification titile
//            String contentTitle = appname;

            // create and send notificaiton
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(appContext)
                    .setSmallIcon(appContext.getApplicationInfo().icon)
                    .setWhen(System.currentTimeMillis())
                    .setAutoCancel(true);

//            Intent msgIntent = appContext.getPackageManager().getLaunchIntentForPackage(packageName);
//            msgIntent.setClass(appContext,ChatActivity.class);
            Intent msgIntent = new Intent(appContext,ChatActivity.class);
            msgIntent.putExtra("name",username);
            msgIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            PendingIntent pendingIntent = PendingIntent.getActivity(appContext, notifyID, msgIntent,PendingIntent.FLAG_UPDATE_CURRENT);

            // prepare latest event info section
//            notificationNum++;
//            fromUsers.add(message.getFrom());
//
//            int fromUsersNum = fromUsers.size();

            String appName = appContext.getResources().getString(R.string.app_name);
            String msgEnd = appContext.getResources().getString(R.string.notification_end);
            String msgFromHint = appContext.getResources().getString(R.string.notification_from_hint);
            mBuilder.setContentTitle(appName+":"+msgFromHint+message.getFrom()+msgEnd);
//            mBuilder.setTicker(notifyText);
            mBuilder.setContentText(notifyText);
            mBuilder.setContentIntent(pendingIntent);
            // mBuilder.setNumber(notificationNum);
            Notification notification = mBuilder.build();
            notificationManager.notify(notifyID,notification);
//            if (isForeground) {
//                notificationManager.notify(foregroundNotifyID, notification);
////                notificationManager.cancel(foregroundNotifyID);
//            } else {
//                notificationManager.notify(notifyID, notification);
//            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void  sendNotification(Context context) {
//            appContext = context;
            Log.d(TAG,"-->appContext:"+appContext);
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(appContext);
                    mBuilder.setSmallIcon(appContext.getApplicationInfo().icon)
                    .setWhen(System.currentTimeMillis())
                    .setAutoCancel(true);

            Intent msgIntent = new Intent(appContext,ChatActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(appContext, notifyID, msgIntent,PendingIntent.FLAG_UPDATE_CURRENT);

            String appName = appContext.getResources().getString(R.string.app_name);
            String msgEnd = appContext.getResources().getString(R.string.notification_end);
            String msgFromHint = appContext.getResources().getString(R.string.notification_from_hint);
            mBuilder.setContentTitle(appName);
            mBuilder.setContentText("This is a notification");
            mBuilder.setContentIntent(pendingIntent);
            Notification notification = mBuilder.build();
            notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(notifyID,notification);
        }

    /**
     */
    public void viberateAndPlayTone(EMMessage message) {
        if(EMChatManager.getInstance().isSlientMessage(message)){
            return;
        }

        HXSDKModel model = HXSDKHelper.getInstance().getModel();
        if(!model.getSettingMsgNotification()){
            return;
        }

        if (System.currentTimeMillis() - lastNotifiyTime < 1000) {
            // received new messages within 2 seconds, skip play ringtone
            return;
        }

        try {
            lastNotifiyTime = System.currentTimeMillis();

            // whether phone is in silent mode
            if (audioManager.getRingerMode() == AudioManager.RINGER_MODE_SILENT) {
                EMLog.e(TAG, "in slient mode now");
                return;
            }

            if(model.getSettingMsgVibrate()){
                long[] pattern = new long[] { 0, 180, 80, 120 };
                vibrator.vibrate(pattern, -1);
            }

            if(model.getSettingMsgSound()){
                if (ringtone == null) {
                    Uri notificationUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

                    ringtone = RingtoneManager.getRingtone(appContext, notificationUri);
                    if (ringtone == null) {
                        EMLog.d(TAG, "cant find ringtone at:" + notificationUri.getPath());
                        return;
                    }
                }

                if (!ringtone.isPlaying()) {
                    String vendor = Build.MANUFACTURER;

                    ringtone.play();
                    // for samsung S3, we meet a bug that the phone will
                    // continue ringtone without stop
                    // so add below special handler to stop it after 3s if
                    // needed
                    if (vendor != null && vendor.toLowerCase().contains("samsung")) {
                        Thread ctlThread = new Thread() {
                            public void run() {
                                try {
                                    Thread.sleep(3000);
                                    if (ringtone.isPlaying()) {
                                        ringtone.stop();
                                    }
                                } catch (Exception e) {
                                }
                            }
                        };
                        ctlThread.run();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     *  set NotificationInfoProvider
     *
     * @param provider
     */
    public void setNotificationInfoProvider(HXNotificationInfoProvider provider) {
        notificationInfoProvider = provider;
    }

    public interface HXNotificationInfoProvider {
        String getDisplayedText(EMMessage message);

        String getLatestText(EMMessage message, int fromUsersNum, int messageNum);

        String getTitle(EMMessage message);

        int getSmallIcon(EMMessage message);

        Intent getLaunchIntent(EMMessage message);
    }
}
