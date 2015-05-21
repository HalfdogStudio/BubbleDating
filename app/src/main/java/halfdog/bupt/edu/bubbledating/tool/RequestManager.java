package halfdog.bupt.edu.bubbledating.tool;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by andy on 2015/5/21.
 */
public class RequestManager {
    private static RequestQueue mRequestQueue;


    // no instances
    private RequestManager(){

    }

    public  static void init(Context context){
        mRequestQueue = Volley.newRequestQueue(context);
    }

    public static RequestQueue getInstance(Context context){
        if(mRequestQueue != null){
            return mRequestQueue;
        }else{
            throw new IllegalStateException("mRequestQueue not initialized!");
        }
    }
}
