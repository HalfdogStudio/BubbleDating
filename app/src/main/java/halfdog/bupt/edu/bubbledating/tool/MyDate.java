package halfdog.bupt.edu.bubbledating.tool;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by andy on 2015/5/4.
 */
public class MyDate {
    private static final String TAG = "MyDate";

    public static final long MINUTE = 1000*60;
    public static final long HOUR = MINUTE*60;
    public static final long DAY = HOUR*24;
    public static final long MONTH = DAY*30;
    public static final long YEAR = 365*DAY;


    public static String getCurSimpleDateFormate(){
        SimpleDateFormat now = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return now.format(new Date());
    }

    public static Date parseSimpleDateFormate(String s){
        Date date = null;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            date = dateFormat.parse(s);
            return date;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    public static Date getCurrentDate(){
        return new Date();
    }

    public static String diffDate(Date d1, Date d2){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Log.d(TAG,"-->d1:"+dateFormat.format(d1)+",d2:"+dateFormat.format(d2));
        Log.d(TAG,"-->d1:"+d1.getTime()+",d2:"+d2.getTime()+",(d1-d2):"+(d1.getTime()-d2.getTime()+
                ",(d1-d2)/day:"+(d1.getTime()-d2.getTime())/DAY));
        // date 1 >= date 2
        StringBuilder ans = new StringBuilder();
        long t1 =d1.getTime();
        long t2 = d2.getTime();
        if(t2 > t1) return "error";
        long diff = t1 - t2;
        if(diff >= YEAR){
            long mYearCount = diff/YEAR;
            diff -= mYearCount * YEAR;
            ans.append(""+mYearCount+" 年前");
            return ans.toString();
        }else if(diff >= DAY){
            long mDayCount = diff/DAY;
            diff -= mDayCount*DAY;
            ans.append("" + mDayCount + " 天前");
            return ans.toString();
        }else if(diff >= HOUR){
            long mHourCount = diff/HOUR;
            diff -= mHourCount * HOUR;
            ans.append("" + mHourCount + " 小时前");
            return ans.toString();
        }
        if(diff >= MINUTE){
            long mMinuteCount = diff/MINUTE;
            diff -= mMinuteCount * MINUTE;
            ans.append(""+ mMinuteCount + " 分钟前");
            return ans.toString();
        }else{
            return "刚刚";
        }


    }
}
