package halfdog.bupt.edu.bubbledating.tool;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;

import halfdog.bupt.edu.bubbledating.R;
import halfdog.bupt.edu.bubbledating.entity.BubbleDatingApplication;

/**
 * Created by andy on 2015/4/27.
 */
public class ImageMerger {
    public static final String TAG = "ImageMerger";
    public static Bitmap addTextOnBitmap(String string, int drawableResId,Context context){
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(),drawableResId);
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
        if(string.length()>4){
            string = string.substring(0,3);
        }
        canvas.drawText(string,width/10.0f,height/2.0f,paint);
        canvas.save(Canvas.ALL_SAVE_FLAG);
        canvas.restore();
        return newBit;

    }
}
