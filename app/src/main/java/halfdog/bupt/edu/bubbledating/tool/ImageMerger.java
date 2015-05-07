package halfdog.bupt.edu.bubbledating.tool;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.text.TextUtils;
import android.util.Log;

import halfdog.bupt.edu.bubbledating.R;
import halfdog.bupt.edu.bubbledating.entity.BubbleDatingApplication;

/**
 * Created by andy on 2015/4/27.
 */
public class ImageMerger {
    public static final String TAG = "ImageMerger";
    public static Bitmap addTextOnBitmap(String name,String gender,Context context){

        Bitmap bitmap = null;
        /*
        *       use equal method to judge if two Object instances are equal
        * */
        if(TextUtils.equals(gender,"m")){
            bitmap = BitmapFactory.decodeResource(context.getResources(),R.mipmap.ic_locator_m_2);
        }else if(TextUtils.equals(gender,"f")){
            bitmap = BitmapFactory.decodeResource(context.getResources(),R.mipmap.ic_locator_w_2);
        }else{
            Log.e(TAG,"-->unknown gender:"+gender);
        }

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        Log.d(TAG,"-->bit map width :"+width);
        Log.d(TAG,"-->bit map width :"+height);
        Bitmap newBit = Bitmap.createBitmap(width,height, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(newBit);

        Paint paint = new Paint();

        canvas.drawBitmap(bitmap,0,0,paint);
        paint.setColor(Color.WHITE);
        paint.setTextSize(context.getResources().getDimension(R.dimen.ic_locator_text_size));
        if(name.length()>4){
            name = name.substring(0,4);
        }
        canvas.drawText(name,width/4.0f,height/2.0f,paint);
        canvas.save(Canvas.ALL_SAVE_FLAG);
        canvas.restore();
        return newBit;

    }
}
