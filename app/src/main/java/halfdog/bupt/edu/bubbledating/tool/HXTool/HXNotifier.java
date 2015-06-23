package halfdog.bupt.edu.bubbledating.tool.HXTool;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import java.util.Locale;

import halfdog.bupt.edu.bubbledating.BubbleDatingApplication;
import halfdog.bupt.edu.bubbledating.R;
import halfdog.bupt.edu.bubbledating.activity.ChatActivity;
import halfdog.bupt.edu.bubbledating.activity.MainActivity;
import halfdog.bupt.edu.bubbledating.constants.Configuration;
import halfdog.bupt.edu.bubbledating.entity.ChatMsgEntity;
import halfdog.bupt.edu.bubbledating.fragment.dummy.MessageFragment;
import halfdog.bupt.edu.bubbledating.tool.DataCache;
import halfdog.bupt.edu.bubbledating.tool.MyDate;
import halfdog.bupt.edu.bubbledating.tool.ProcessManager;

/**
 * 新消息提醒class
 * 2.1.8把新消息提示相关的api移除出sdk，方便开发者自由修改
 * 开发者也可以继承此类实现相关的接口
 *
 * this class is subject to be inherited and implement the relative APIs
 */
public class HXNotifier {
    private final static String TAG = "notify";

    Ringtone ringtone = null;

    public final static String[] msg_eng = { "sent a message", "sent a picture", "sent a voice",
            "sent location message", "sent a video", "sent a file", "%1 contacts sent %2 messages"
    };
    public final static String[] msg_ch = { "发来一条消息", "发来一张图片", "发来一段语音", "发来位置信息", "发来一个视频", "发来一个文件",
            "%1个联系人发来%2条消息"
    };

    public static int notifyID = 0525; // start notification id
    public static int foregroundNotifyID = 0555;

    public NotificationManager notificationManager = null;

    public HashSet<String> fromUsers = new HashSet<String>();
    public int notificationNum = 0;

    public Context appContext;
    public String packageName;
    public String[] msgs;
    public long lastNotifiyTime;
    public AudioManager audioManager;
    public Vibrator vibrator;
    public HXNotificationInfoProvider notificationInfoProvider;



    public HXNotifier() {
    }

    /**
     * 开发者可以重载此函数
     * this function can be override
     * @param context
     * @return
     */
    public HXNotifier init(Context context){
        appContext = context;
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Log.d(TAG,"-->init NotificationManager in HXNotifier");

        packageName = appContext.getApplicationInfo().packageName;
        if (Locale.getDefault().getLanguage().equals("zh")) {
            msgs = msg_ch;
        } else {
            msgs = msg_eng;
        }

        audioManager = (AudioManager) appContext.getSystemService(Context.AUDIO_SERVICE);
        vibrator = (Vibrator) appContext.getSystemService(Context.VIBRATOR_SERVICE);

        return this;
    }

    /**
     * 开发者可以重载此函数
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

    /**
     * 处理新收到的消息，然后发送通知
     *
     * 开发者可以重载此函数
     * this function can be override
     *
     * @param message
     */
    public synchronized void onNewMsg(final EMMessage message,Context context,SQLiteDatabase db,String messageContent) {
//        if(EMChatManager.getInstance().isSlientMessage(message)){
//            return;
//        }
//        Log.d(TAG,"-->on New Msg in HX Notifier");
        // 判断app是否在后台
        if(ProcessManager.isBackGround(context)){
            Log.d(TAG, "--> process is in background");
            sendNotification(message, false, messageContent);
            ChatMsgEntity entity = new ChatMsgEntity(message.getTo(),message.getFrom(),
                    messageContent,MyDate.getCurSimpleDateFormate().toString(),true);

            DataCache.updateUsrMsgAndContacListInMemory(entity);

            ChatActivity.sendOrReceiveUiMsg(entity, true);
            DataCache.updateUsrMsgAndContactListInDB(entity, db);
        }else{
            Log.d(TAG, "--> app is in foreground");
            sendNotification(message, true, messageContent);
            ChatMsgEntity entity = new ChatMsgEntity(message.getTo(),message.getFrom(),
                    messageContent,MyDate.getCurSimpleDateFormate().toString(),true);

            DataCache.updateUsrMsgAndContacListInMemory(entity);



            /*应该判断当前Activity是不是ChatActivity, 若是， 发送消息， 若不是， 只更新DataCache 和 sqlite的内容，
            * 否则可能出现ChatActivity还没实例化，就已然调用ChatActivity的方法的错误。*/
            Log.d(TAG, "-->current activity:" + BubbleDatingApplication.getCurrentActivity());
            if(BubbleDatingApplication.getCurrentActivity() instanceof  ChatActivity && TextUtils.equals(message.getFrom(),ChatActivity.chatter)){
                Message msg = ChatActivity.mHandler.obtainMessage();
                msg.what = Configuration.UPDATE_CHAT_ACTIVITY_CONTACT;
                msg.obj = entity;
                ChatActivity.mHandler.sendMessage(msg);
            }else if (BubbleDatingApplication.getCurrentActivity() instanceof MainActivity && MainActivity.currentFragment instanceof MessageFragment){
                Log.d(TAG,"--> refresh message fragment.");
                Message msg = MessageFragment.mhandler.obtainMessage(Configuration.UPDATE_MESSAGE_FRAGMENT,entity);
                MessageFragment.mhandler.sendMessage(msg);
            }
            /* 执行到中间部分会中断*/
            DataCache.updateUsrMsgAndContactListInDB(entity, db);





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
     * 发送通知栏提示
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

            PackageManager packageManager = appContext.getPackageManager();
            String appname = (String) packageManager.getApplicationLabel(appContext.getApplicationInfo());

            // notification titile
            String contentTitle = appname;
//            if (notificationInfoProvider != null) {
//                String customNotifyText = notificationInfoProvider.getDisplayedText(message);
//                String customCotentTitle = notificationInfoProvider.getTitle(message);
//                if (customNotifyText != null){
//                    // 设置自定义的状态栏提示内容
//                    notifyText = customNotifyText;
//                }
//
//                if (customCotentTitle != null){
//                    // 设置自定义的通知栏标题
//                    contentTitle = customCotentTitle;
//                }
//            }

            // create and send notificaiton
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(appContext)
                    .setSmallIcon(appContext.getApplicationInfo().icon)
                    .setWhen(System.currentTimeMillis())
                    .setAutoCancel(true);

            Intent msgIntent = appContext.getPackageManager().getLaunchIntentForPackage(packageName);
//            if (notificationInfoProvider != null) {
//                // 设置自定义的notification点击跳转intent
//                msgIntent = notificationInfoProvider.getLaunchIntent(message);
//            }

            PendingIntent pendingIntent = PendingIntent.getActivity(appContext, notifyID, msgIntent,PendingIntent.FLAG_UPDATE_CURRENT);

            // prepare latest event info section
            notificationNum++;
            fromUsers.add(message.getFrom());

            int fromUsersNum = fromUsers.size();

            String appName = appContext.getResources().getString(R.string.app_name);
            String msgEnd = appContext.getResources().getString(R.string.notification_end);
            String msgFromHint = appContext.getResources().getString(R.string.notification_from_hint);
            mBuilder.setContentTitle(appName+":"+msgFromHint+message.getFrom()+msgEnd);
//            mBuilder.setTicker(notifyText);
            mBuilder.setContentText(notifyText);
            mBuilder.setContentIntent(pendingIntent);
            // mBuilder.setNumber(notificationNum);
            Notification notification = mBuilder.build();

            if (isForeground) {
                notificationManager.notify(foregroundNotifyID, notification);
//                notificationManager.cancel(foregroundNotifyID);
            } else {
                notificationManager.notify(notifyID, notification);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 手机震动和声音提示
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

            // 判断是否处于静音模式
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
     * 设置NotificationInfoProvider
     *
     * @param provider
     */
    public void setNotificationInfoProvider(HXNotificationInfoProvider provider) {
        notificationInfoProvider = provider;
    }

    public interface HXNotificationInfoProvider {
        /**
         * 设置发送notification时状态栏提示新消息的内容(比如Xxx发来了一条图片消息)
         *
         * @param message
         *            接收到的消息
         * @return null为使用默认
         */
        String getDisplayedText(EMMessage message);

        /**
         * 设置notification持续显示的新消息提示(比如2个联系人发来了5条消息)
         *
         * @param message
         *            接收到的消息
         * @param fromUsersNum
         *            发送人的数量
         * @param messageNum
         *            消息数量
         * @return null为使用默认
         */
        String getLatestText(EMMessage message, int fromUsersNum, int messageNum);

        /**
         * 设置notification标题
         *
         * @param message
         * @return null为使用默认
         */
        String getTitle(EMMessage message);

        /**
         * 设置小图标
         *
         * @param message
         * @return 0使用默认图标
         */
        int getSmallIcon(EMMessage message);

        /**
         * 设置notification点击时的跳转intent
         *
         * @param message
         *            显示在notification上最近的一条消息
         * @return null为使用默认
         */
        Intent getLaunchIntent(EMMessage message);
    }
}
