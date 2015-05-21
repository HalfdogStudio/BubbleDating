package halfdog.bupt.edu.bubbledating.cache.image;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;

import com.android.volley.toolbox.ImageLoader;

import halfdog.bupt.edu.bubbledating.tool.RequestManager;

/**
 * Created by andy on 2015/5/21.
 */
public class ImageCacheManager {
    /*
    *       Implementation of volley's ImageCache Interface.
    *
    *       Volley recommends an L1 non-blocking cache which is the default MEMORY Cache Type.
    *       Volley itself includes a L2 disk cache out of box, but you can still use a disk cache
    *       as an L1 cache , you can live with potential i/o blocking.
    *
    * */


     private ImageCacheManager(){}

     public enum CacheType{
        DISK,
        MEMORY
    }

    /*
    *       ImageCacheManager single instance
    * */
    public static ImageCacheManager mInstance = new ImageCacheManager();


    /*
    *       volley image loader
    * */
    private ImageLoader mImageLoader;

    /*
    *       image cache implementation
    * */
    private ImageLoader.ImageCache  mImageCache;

    public static ImageCacheManager getInstance(){
        return mInstance;
    }

    /*
    *       initialization method, must be called before prior to use
    * */

    public void init(Context context, String uniqueName, int cacheSize, Bitmap.CompressFormat compressFormat,
                     int quality, CacheType type){
        switch(type){
            case DISK:
                mImageCache = new DiskLruImageCache(context,uniqueName,cacheSize,compressFormat,quality);
                break;
            case MEMORY:
                mImageCache = new BitmapLruImageCache(cacheSize);
                break;
            default:
                mImageCache = new BitmapLruImageCache(cacheSize);
                break;
        }
        mImageLoader = new ImageLoader(RequestManager.getInstance(context),mImageCache);
    }

    public Bitmap getBitmap(String url){
       try{
           return mImageCache.getBitmap(url);
       }catch(NullPointerException e){
           throw new IllegalStateException("Disk cache not initialized");
       }
    }

    public void putBitmap(String url,Bitmap bitmap){
        try{
            mImageCache.putBitmap(createKey(url),bitmap);
        }catch(NullPointerException e){
            throw new IllegalStateException("Disk cache not initialized");
        }
    }

    public String createKey(String url){
        return String.valueOf(url.hashCode());
    }

    public ImageLoader getmImageLoader(){
        return mImageLoader;
    }

    public void getImage(String url,ImageLoader.ImageListener listener){
        mImageLoader.get(url,listener);
    }



 }
