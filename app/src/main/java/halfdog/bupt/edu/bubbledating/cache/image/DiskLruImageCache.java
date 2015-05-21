package halfdog.bupt.edu.bubbledating.cache.image;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.android.volley.toolbox.ImageLoader;
import com.jakewharton.disklrucache.DiskLruCache;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import halfdog.bupt.edu.bubbledating.constants.Mode;

/**
 * Created by andy on 2015/5/21.
 */
public class DiskLruImageCache implements ImageLoader.ImageCache{
    private final String TAG = "DiskLruImageCache";
    private DiskLruCache mDiskLruCache;
    private Bitmap.CompressFormat mCompressFormat = Bitmap.CompressFormat.JPEG;
    private int mCompressQuality = 70;
    private static int IO_BUFFER_SIZE = 10*1024;
    private static int APP_VERSION = 1;
    private static int VALUE_COUNT = 1;

    public DiskLruImageCache(Context context,String uniqueName, int diskCacheSize,
                             Bitmap.CompressFormat compressFormat, int quality){
        try {
            final File diskCacheDir = getDiskCacheDir(context, uniqueName);
            mDiskLruCache = DiskLruCache.open(diskCacheDir,APP_VERSION,VALUE_COUNT,diskCacheSize);
            mCompressFormat = compressFormat;
            mCompressQuality = quality;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public File getDiskCacheDir(Context context, String uniqueName){
        final String cachePath = context.getCacheDir().getPath();
        Log.d(TAG,"getDiskCacheDir:"+cachePath);
        return new File(cachePath + File.separator + uniqueName);
    }

    private boolean writeBitmapToFile(Bitmap bitmap, DiskLruCache.Editor editor) throws IOException {
        OutputStream outputStream = null;
        try{
            outputStream = new BufferedOutputStream(editor.newOutputStream(0),IO_BUFFER_SIZE);
            return bitmap.compress(mCompressFormat,mCompressQuality,outputStream);
        }finally{
            if(outputStream != null){
                outputStream.close();
            }
        }
    }

    @Override
    public Bitmap getBitmap(String s) {
        Bitmap bitmap = null;
        DiskLruCache.Snapshot snapshot = null;

        try {
            snapshot = mDiskLruCache.get(s);
            if(snapshot == null){
                return null;
            }

            final InputStream inputStream = snapshot.getInputStream(0);
            if(inputStream != null ){
                final BufferedInputStream bufferedInputStream =
                        new BufferedInputStream(inputStream,IO_BUFFER_SIZE);
                bitmap = BitmapFactory.decodeStream(bufferedInputStream);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally{
            if(snapshot != null){
                snapshot.close();
            }
        }
        Log.d(TAG,"test cache:"+bitmap==null?"":"load image from :"+s);

        return bitmap;
    }

    @Override
    public void putBitmap(String s, Bitmap bitmap) {

        DiskLruCache.Editor editor = null;

        try {
            editor = mDiskLruCache.edit(s);
            if(editor == null){
                return ;
            }

            if(writeBitmapToFile(bitmap,editor)){
                mDiskLruCache.flush();
                editor.commit();
                if(Mode.DEBUG){
                    Log.d(TAG,"cache test disk, put image on disk cache:"+s );
                }

            }else{
                editor.abort();
                if(Mode.DEBUG){
                    Log.d(TAG,"cache test disk, error on put image on disk cache: "+ s);
                }
            }

        } catch (IOException e) {
            if(Mode.DEBUG){
                Log.d(TAG,"cache test disk, error on put image on disk cache: "+ s);
            }
            try {
                if(editor != null){
                    editor.abort();
                }
            }catch(IOException ignored){}
        }

    }

    public File getCacheFolder(){
        return mDiskLruCache.getDirectory();
    }

    public void clearCache(){
        if(Mode.DEBUG){
            Log.d(TAG,"disk cache CLEARED");
        }
        try {
            mDiskLruCache.delete();
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    public boolean containsKey(String key){
        boolean contains = false;
        DiskLruCache.Snapshot snapshot = null;
        try {
            snapshot = mDiskLruCache.get(key);
            contains = (snapshot != null);
        }catch(IOException e){
            e.printStackTrace();

        }finally{
            if(snapshot != null){
                snapshot.close();
            }
        }
        return contains;
    }




}
