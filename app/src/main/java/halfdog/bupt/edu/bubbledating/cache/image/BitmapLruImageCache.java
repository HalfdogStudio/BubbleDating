package halfdog.bupt.edu.bubbledating.cache.image;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;
import android.util.Log;

import com.android.volley.toolbox.ImageLoader;

/**
 * Created by andy on 2015/5/21.
 */
public class BitmapLruImageCache extends LruCache<String,Bitmap> implements ImageLoader.ImageCache {
    private final String TAG = "BitmapLruImageCache";

    /**
     * @param maxSize for caches that do not override {@link #sizeOf}, this is
     *                the maximum number of entries in the cache. For all other caches,
     *                this is the maximum sum of the sizes of the entries in this cache.
     */
    public BitmapLruImageCache(int maxSize) {
        super(maxSize);
    }

    @Override
    protected int sizeOf(String key, Bitmap value) {
        return value.getRowBytes() * value.getHeight();
    }

    @Override
    public Bitmap getBitmap(String s) {
        Log.d(TAG,"get bitmap from memory cache");
        return get(s);
    }

    @Override
    public void putBitmap(String s, Bitmap bitmap) {
        Log.d(TAG,"put bitmap into memory cache");
        put(s,bitmap);
    }
}
