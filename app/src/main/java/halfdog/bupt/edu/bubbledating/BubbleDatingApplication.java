package halfdog.bupt.edu.bubbledating;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.util.Log;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.model.LatLng;
import com.easemob.chat.EMChat;
import com.easemob.chat.EMChatConfig;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMChatOptions;

import java.util.Iterator;
import java.util.List;

import halfdog.bupt.edu.bubbledating.cache.image.ImageCacheManager;
import halfdog.bupt.edu.bubbledating.constants.Mode;
import halfdog.bupt.edu.bubbledating.entity.UserEntity;
import halfdog.bupt.edu.bubbledating.tool.HXTool.HXSDKHelper;
import halfdog.bupt.edu.bubbledating.tool.RequestManager;

/**
 * Created by andy on 2015/4/25.
 */
public class BubbleDatingApplication extends Application {
    public static final String TAG = "BubbleDatingApplication";
    public static final Bitmap.CompressFormat IMAGE_CACHE_COMPRESS_FORMAT = Bitmap.CompressFormat.PNG;
    public static final int IMAGE_CACHE_QUALITY = 100;
    public static final ImageCacheManager.CacheType CACHE_TYPE = ImageCacheManager.CacheType.MEMORY;

    public static UserEntity userEntity = null;
    public static  LatLng userLatLng;
    public static LocationClient mLocationClient = null;

    private static Activity mCurrentActivity = null;



    public static int screenWidth;
    public static int screenHeight;
    public static int densityDpi;
    public static float density;
    public static int mCacheSize = 1024 * 1024 * 8;
    public static int mode = Mode.ONLINE_MODE;


    public BDLocationListener listener = new MyLocationListener();

    @Override
    public void onCreate() {
        super.onCreate();
        //init user entity
        userEntity = null;
        //init cache size
        int maxMemory = (int)Runtime.getRuntime().maxMemory();
        mCacheSize = maxMemory/8;
//        if(Mode.DEBUG){
//            Log.d(TAG,"-->mCacheSize:"+mCacheSize);
//        }
        // init RequestManager
        RequestManager.init(getApplicationContext());
        // init ImageCacheManager
        ImageCacheManager.getInstance().init(getApplicationContext(), this.getPackageCodePath(), mCacheSize,
                IMAGE_CACHE_COMPRESS_FORMAT, IMAGE_CACHE_QUALITY, CACHE_TYPE);
        initBaiduMap();
        initHX();


    }

    public void initBaiduMap(){
        //初始化百度地图
        SDKInitializer.initialize(getApplicationContext());
        mLocationClient = new LocationClient(getApplicationContext());
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);//设置定位模式
        option.setCoorType("bd09ll");//返回的定位结果是百度经纬度,默认值gcj02
        option.setScanSpan(5000);//设置发起定位请求的间隔时间为5000ms
        option.setIsNeedAddress(true);//返回的定位结果包含地址信息
        option.setNeedDeviceDirect(true);//返回的定位结果包含手机机头的方向
        mLocationClient.setLocOption(option);
        mLocationClient.registerLocationListener(listener);
        beginLocate();
    }

    public void initHX(){
        Context appContext = getApplicationContext();
        int pid = android.os.Process.myPid();
        String processAppName = getAppName(pid);
        // 如果app启用了远程的service，此application:onCreate会被调用2次
        // 为了防止环信SDK被初始化2次，加此判断会保证SDK被初始化1次
        // 默认的app会在以包名为默认的process name下运行，如果查到的process name不是app的process name就立即返回

        if (processAppName == null ||!processAppName.equalsIgnoreCase("halfdog.bupt.edu.bubbledating")) {
            Log.e(TAG, "enter the service process!");
            //"com.easemob.chatuidemo"为demo的包名，换到自己项目中要改成自己包名

            // 则此application::onCreate 是被service 调用的，直接返回
            return;
        }
        EMChat.getInstance().init(appContext);
        /**
         * debugMode == true 时为打开，sdk 会在log里输入调试信息
         * @param debugMode
         * 在做代码混淆的时候需要设置成false
         */
        EMChat.getInstance().setDebugMode(true);//在做打包混淆时，要关闭debug模式，如果未被关闭，则会出现程序无法运行问题
//        Log.d(TAG,"-->set EMChat, debug mode=true");

        HXSDKHelper.getInstance().onInit(appContext);

    }

    public static void beginLocate(){
        mLocationClient.start();
        mLocationClient.requestLocation();
        Log.d(TAG,"-->开始定位");
    }

    public class MyLocationListener implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            if (location == null)
                return ;

            userLatLng = new LatLng(location.getLatitude(),location.getLongitude());
            StringBuffer sb = new StringBuffer(256);
            sb.append("time : ");
            sb.append(location.getTime());
            sb.append("\nerror code : ");
            sb.append(location.getLocType());
            sb.append("\nlatitude : ");
            sb.append(location.getLatitude());
            sb.append("\nlontitude : ");
            sb.append(location.getLongitude());
            sb.append("\nradius : ");
            sb.append(location.getRadius());
            if (location.getLocType() == BDLocation.TypeGpsLocation){
                sb.append("\nspeed : ");
                sb.append(location.getSpeed());
                sb.append("\nsatellite : ");
                sb.append(location.getSatelliteNumber());
            } else if (location.getLocType() == BDLocation.TypeNetWorkLocation){
                sb.append("\naddr : ");
                sb.append(location.getAddrStr());
            }

            Log.d("", "-->" + sb.toString());

            mLocationClient.stop();
            Log.d(TAG,"-->停止定位");
        }


    }

    public static String getApplicationName(Context context){
        int stringId = context.getApplicationInfo().labelRes;
        if(Mode.DEBUG){
            Log.d(TAG,"--> app name:"+context.getString(stringId));
        }
        return context.getString(stringId);
    }

    public static String getAppVersion(Context context){
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(),0);
            if(Mode.DEBUG){
                Log.d(TAG,"-->version name:"+packageInfo.versionName);
            }
            return "version "+packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "";
    }

    public  String getAppName(int pID) {
        String processName = null;
        ActivityManager am = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> l = am.getRunningAppProcesses();
        Iterator i = l.iterator();
        PackageManager pm = this.getPackageManager();
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

    public static  Activity getCurrentActivity(){
        return mCurrentActivity;
    }
    public static void setCurrentActivity(Activity mCur){
        mCurrentActivity = mCur;
    }

}
