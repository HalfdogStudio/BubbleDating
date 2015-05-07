package halfdog.bupt.edu.bubbledating.constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by andy on 2015/5/4.
 */
public class Offline {
    public static final String OFFLINE_DB = "offline.db";
    public static JSONArray offline_people_around;

    static{
        offline_people_around = new JSONArray();
        JSONObject candi1 = new JSONObject();
        JSONObject candi2 = new JSONObject();
        try {
            candi1.put("u_id",34);
            candi1.put("u_name","loly");
            candi1.put("u_posttime","2015-04-27 20:09:21.0");
            candi1.put("u_gender","f");
            candi1.put("u_invi","一起看速七，然后去速八");
            candi1.put("u_loc_lat","39.96575");
            candi1.put("u_loc_long","116.365005");

            candi2.put("u_id",33);
            candi2.put("u_name","joseph");
            candi2.put("u_posttime","2015-05-2 20:09:21.0");
            candi2.put("u_gender","m");
            candi2.put("u_invi","今晚九点，北邮游泳馆，想找个MM一起游泳~");
            candi2.put("u_loc_lat","39.96875");
            candi2.put("u_loc_long","116.364905");
            offline_people_around.put(candi1);
            offline_people_around.put(candi2);
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }
}
