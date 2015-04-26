package halfdog.bupt.edu.bubbledating.entity;

import android.app.Application;
import android.content.Context;

import com.baidu.mapapi.SDKInitializer;

/**
 * Created by andy on 2015/4/25.
 */
public class BubbleDatingApplication extends Application {
    public static User user;

    @Override
    public void onCreate() {
        super.onCreate();
        user = null;
        SDKInitializer.initialize(getApplicationContext());

    }

}
