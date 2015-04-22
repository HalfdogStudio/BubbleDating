package halfdog.bupt.edu.bubbledating.activity;

import android.graphics.Bitmap;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.LruCache;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import halfdog.bupt.edu.bubbledating.R;


public class MainActivity extends ActionBarActivity {
    public static final String TAG = "MainActivity";
    ImageView imageView;
    NetworkImageView networkImageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView = (ImageView)findViewById(R.id.imageview);
        networkImageView = (NetworkImageView)findViewById(R.id.network_image_view);
        networkImageView.setDefaultImageResId(R.drawable.ic_launcher);
        networkImageView.setErrorImageResId(R.drawable.abc_ab_share_pack_mtrl_alpha);

        int maxMemeory = (int)(Runtime.getRuntime().maxMemory()/1024/1024);
        Log.d(TAG,"--> max memory:"+maxMemeory+" Mb");
        Log.d(TAG,"-->found image view");
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        Log.d(TAG, "-->new request queue");
        ImageLoader loader = new ImageLoader(requestQueue,new BitmapCache());
        ImageLoader.ImageListener listener = ImageLoader.getImageListener(imageView,R.drawable.ic_launcher,R.drawable.abc_ab_share_pack_mtrl_alpha);
        loader.get("http://img01.taobaocdn.com/bao/uploaded/i1/T1avCJFMlhXXXXXXXX_!!0-item_pic.jpg_110x90.jpg",listener);
        networkImageView.setImageUrl("http://img01.taobaocdn.com/bao/uploaded/i1/T1avCJFMlhXXXXXXXX_!!0-item_pic.jpg_110x90.jpg",loader);
    }

    public class BitmapCache implements ImageLoader.ImageCache{
        LruCache<String,Bitmap> mCache;
        public BitmapCache(){
            int maxSize = 1024*1024*4; // 4MB
            mCache = new LruCache<String , Bitmap>(maxSize){
                @Override
                protected int sizeOf(String key, Bitmap value) {
                    return value.getRowBytes();
                }
            };
        }
        @Override
        public Bitmap getBitmap(String s) {
            return mCache.get(s);
        }

        @Override
        public void putBitmap(String s, Bitmap bitmap) {
            mCache.put(s,bitmap);
        }
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
