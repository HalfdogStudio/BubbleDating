package halfdog.bupt.edu.bubbledating.fragment.dummy;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.PaintDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;


import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.overlayutil.OverlayManager;
import com.gc.materialdesign.views.ButtonRectangle;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import halfdog.bupt.edu.bubbledating.R;
import halfdog.bupt.edu.bubbledating.activity.ChatActivity;
import halfdog.bupt.edu.bubbledating.cache.image.ImageCacheManager;
import halfdog.bupt.edu.bubbledating.constants.Configuration;
import halfdog.bupt.edu.bubbledating.constants.Mode;
import halfdog.bupt.edu.bubbledating.constants.Offline;
import halfdog.bupt.edu.bubbledating.BubbleDatingApplication;
import halfdog.bupt.edu.bubbledating.tool.ImageMerger;
import halfdog.bupt.edu.bubbledating.tool.MyDate;
import halfdog.bupt.edu.bubbledating.tool.RequestManager;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link DateFragment.OnDatingFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DateFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DateFragment extends  Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    public final String REQUEST_PEOPLE_AROUND = Configuration.SERVER_IP+"/BubbleDatingServer/HandlePeopleAround";


    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    public final String TAG = "DateFragment";

    private MapView mMapView;
    private BaiduMap mMap;
    private Context context;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnDatingFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DatingFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DateFragment newInstance(String param1, String param2) {
        DateFragment fragment = new DateFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public DateFragment() {
        // Required empty public constructor

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        SDKInitializer.initialize(getApplicationContext())
        context = getActivity();
        Log.d("","--> on create");
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.d(" ","-->on create view");
        View view  = inflater.inflate(R.layout.fragment_dating,container,false);
        mMapView = (MapView)view.findViewById(R.id.bmapView);
        mMap = mMapView.getMap();
        mMap.clear();
        mMap.setOnMarkerClickListener(onMarkerClickListener);
        MapStatusUpdate update = MapStatusUpdateFactory.newLatLngZoom(BubbleDatingApplication.userLatLng,17);
        mMap.setMapStatus(update);
        /*
        *       request to get people around
        * */
        if(BubbleDatingApplication.mode == Mode.OFFLINE_MODE){
            Log.d(TAG,"-->OFFLINE branch");
            JSONObject item = null;
            for(int i = 0; i < Offline.offline_people_around.length(); i ++){
                try {
                    item = (JSONObject)Offline.offline_people_around.get(i);
                    int uId = item.getInt("u_id");
                    String uName = item.getString("u_name");
                    String uInviName = item.getString("u_invi");
                    String uGender = item.getString("u_gender");
                    double uLat = item.getDouble("u_loc_lat");
                    double uLong = item.getDouble("u_loc_long");
                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date uDate = df.parse(item.getString("u_posttime"));
                    Date now = MyDate.getCurrentDate();
                    //上传时间距离现在的差 eg: XXX天前， XXX 小时前
                    String dateDiff = MyDate.diffDate(now,uDate);

                    Log.d(TAG,"-->uId:"+uId+"  uName:"+uName+" uInviName:"+uInviName+" uGender:"+uGender+
                            " uLat:"+uLat+" uLong:"+uLong+" uDate:"+uDate.toString());
                    Log.d(TAG,"-->uGender:"+uGender);
                    // add loc icon
                    if(uLat == 0 && uLong == 0){
                        continue;
                    }
                    Bitmap photoOfHead = ImageMerger.addTextOnBitmap(uName,uGender,getActivity());
                    BitmapDescriptor bitmap = BitmapDescriptorFactory.fromBitmap(photoOfHead);
                    Bundle extraInfo = new Bundle();
                    extraInfo.putInt("uId",uId);
                    extraInfo.putString("uName", uName);
                    extraInfo.putString("uGender",uGender);
                    extraInfo.putString("uInvi",uInviName);
                    extraInfo.putString("uDiffDate", dateDiff);
                    OverlayOptions options = new MarkerOptions().title(""+uId).position(new LatLng(uLat,uLong)).icon(bitmap).extraInfo(extraInfo);
                    mMap.addOverlay(options);
                    mMapManager.addToMap();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }else{
            /*
            *       联网Mode
            * */
//            RequestQueue queue  = Volley.newRequestQueue(getActivity());
            Log.d(TAG,"-->online branch");
            JsonArrayRequest requestPeopleAround = new JsonArrayRequest(Request.Method.GET,REQUEST_PEOPLE_AROUND,responseListener,errorListener);
            RequestManager.getInstance(context).add(requestPeopleAround);
        }


        /*
        *       add loc icon
        * */
//        Bitmap photoOfHead = ImageMerger.addTextOnBitmap("andy","m",getActivity());
//        BitmapDescriptor bitmap = BitmapDescriptorFactory.fromBitmap(photoOfHead);
//        Bundle extraInfo = new Bundle();
//        extraInfo.putString("hobby","swimming");
//        OverlayOptions options = new MarkerOptions().title("1").position(BubbleDatingApplication.userLatLng).icon(bitmap).extraInfo(extraInfo);
//        mMap.addOverlay(options);
        return view;



    }


    OverlayManager mMapManager = new OverlayManager(mMap){

        @Override
        public List<OverlayOptions> getOverlayOptions() {
            return null;
        }

        @Override
        public boolean onMarkerClick(Marker marker) {
            Bundle extra  = marker.getExtraInfo();
            if(extra == null) return true;
            int uId = extra.getInt("uId");
            String gender = extra.getString("uGender");
            String diffDate = extra.getString("uDiffDate");
            final String name = extra.getString("uName");
            String info = extra.getString("uInvi");
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            if(Mode.DEBUG){
                Log.d(TAG,"-->clicked: name:"+name+",gender:"+gender+",gender.equals(f):"+gender.equals("f"));
            }
            View userInfoView = getActivity().getLayoutInflater().inflate(R.layout.user_info_view,null);
            ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams((int)getResources().getDimension(R.dimen.date_fragment_popup_window_width),
                    (int)getResources().getDimension(R.dimen.date_fragment_popup_window_height));
            userInfoView.setLayoutParams(layoutParams);
            TextView mUserName = (TextView)userInfoView.findViewById(R.id.user_info_name);
            ImageView mUserGender = (ImageView)userInfoView.findViewById(R.id.user_info_gender);
            TextView mUserInviContent = (TextView)userInfoView.findViewById(R.id.user_info_invitation_content);
            TextView mUserPosttime = (TextView)userInfoView.findViewById(R.id.user_info_posttime);
            final CircleImageView mUserAvatar = (CircleImageView)userInfoView.findViewById(R.id.user_info_avatar);
            ButtonRectangle mUserChat = (ButtonRectangle)userInfoView.findViewById(R.id.user_info_chat_button);
            mUserName.setText(name);
            mUserPosttime.setText(diffDate);
            if(gender.equals("m")){
                mUserGender.setImageResource(R.mipmap.ic_m);
//                mUserAvatar.setImageDrawable(context.getResources().getDrawable(R.drawable.avatar_default_m));
            }else if (gender.equals("f")){
                mUserGender.setImageResource(R.mipmap.ic_w);
//                mUserAvatar.setImageDrawable(context.getResources().getDrawable(R.drawable.avatar_default_f));
            }
            if(TextUtils.isEmpty(info)){
                mUserInviContent.setVisibility(View.GONE);
            }else{
                mUserInviContent.setText(info);
            }

            /*
            *       load user avatar
            * */
            ImageLoader.ImageListener userAvatorListener = ImageLoader.getImageListener(mUserAvatar,
                    R.drawable.avatar_default_m, R.drawable.avatar_default_f);
            String ServerImgUrl = Configuration.SERVER_IMG_CACHE_DIR + File.separator + name + ".png";
            if(Mode.DEBUG){
                Log.d(TAG, "-->ServerImgUrl of "+mUserName + " is : "+ServerImgUrl);
            }
            ImageCacheManager.getInstance().getImage(ServerImgUrl,userAvatorListener);
            mUserChat.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), ChatActivity.class);
                    intent.putExtra("name",name);
                    startActivity(intent);
                }
            });
            PopupWindow popupWindow = new PopupWindow(userInfoView,(int)getResources().getDimension(R.dimen.date_fragment_popup_window_width),(int)getResources().getDimension(R.dimen.date_fragment_popup_window_height),true);
            //通过设置背景图片可以使popup window出现后能够通过点击旁白或者back键让它消失
            //popupwindow背景设置成为透明色，以防设圆角时显示黑色
            popupWindow.setBackgroundDrawable(new PaintDrawable(Color.TRANSPARENT) );
            popupWindow.setTouchable(true);
            popupWindow.setOutsideTouchable(true);
            popupWindow.showAtLocation(getView(), Gravity.CENTER_HORIZONTAL,0,0);
//            Toast.makeText(getActivity(),"'"+info+"'"+" by "+name,Toast.LENGTH_SHORT).show();
            return true;
        }
    };

    Response.Listener<JSONArray> responseListener = new Response.Listener<JSONArray>() {
        @Override
        public void onResponse(JSONArray jsonArray) {
            Log.d(TAG,"--> get reponse with json array from server ");
            for(int i = 0; i < jsonArray.length(); i ++ ){
                try {

                    JSONObject item = jsonArray.getJSONObject(i);
                    Log.d(TAG,"-->request invitation:"+i+":"+item.toString());
                    int uId = item.getInt("u_id");
                    String uName = item.getString("u_name");
                    String uInviName = item.getString("u_invi");
                    String uGender = item.getString("u_gender");
                    double uLat = item.getDouble("u_loc_lat");
                    double uLong = item.getDouble("u_loc_long");
//                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String uDateDiff = null;
                   try{
                       Date uDate = new Date(item.getLong("u_posttime"));
                       Date now = MyDate.getCurrentDate();
                       //上传时间距离现在的差 eg: XXX天前， XXX 小时前
                       uDateDiff = MyDate.diffDate(now,uDate);
                     }catch(Exception e){
                       Log.d(TAG,"-->exception occurred while parsing posttime");
                   }

                    Log.d(TAG,"-->uId:"+uId+"  uName:"+uName+" uInviName:"+uInviName+" uGender:"+uGender+
                    " uLat:"+uLat+" uLong:"+uLong+" uDateDiff:"+uDateDiff);
                    // add loc icon
                    if(uLat == 0 && uLong == 0){
                        Log.d(TAG,"-->lat or long is 0, continue, username:"+uName);
                        continue;
                    }
                    Bitmap photoOfHead = ImageMerger.addTextOnBitmap(uName,uGender,getActivity());
                    BitmapDescriptor bitmap = BitmapDescriptorFactory.fromBitmap(photoOfHead);
                    Bundle extraInfo = new Bundle();
                    extraInfo.putInt("uId",uId);
                    extraInfo.putString("uName", uName);
                    extraInfo.putString("uGender",uGender);
                    extraInfo.putString("uInvi",uInviName);
                    extraInfo.putString("uDiffDate", uDateDiff );
                    OverlayOptions options = new MarkerOptions().title(""+uId).position(new LatLng(uLat,uLong)).icon(bitmap).extraInfo(extraInfo).visible(true);
                    mMap.addOverlay(options);
                    mMapManager.addToMap();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    };

    Response.ErrorListener errorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError volleyError) {
            Log.d(TAG,"--> ERROR HAPPENED during query of people aroound");
        }
    };

    BaiduMap.OnMarkerClickListener onMarkerClickListener = new BaiduMap.OnMarkerClickListener() {
        @Override
        public boolean onMarkerClick(Marker marker) {
            Bundle extra  = marker.getExtraInfo();
            if(extra == null) return true;
            int uId = extra.getInt("uId");
            String gender = extra.getString("uGender");
            String diffDate = extra.getString("uDiffDate");
            final String name = extra.getString("uName");
            String info = extra.getString("uInvi");
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            if(Mode.DEBUG){
                Log.d(TAG,"-->clicked: name:"+name+",gender:"+gender+",gender.equals(f):"+gender.equals("f"));
            }
            View userInfoView = getActivity().getLayoutInflater().inflate(R.layout.user_info_view,null);
            ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams((int)getResources().getDimension(R.dimen.date_fragment_popup_window_width),
                    (int)getResources().getDimension(R.dimen.date_fragment_popup_window_height));
            userInfoView.setLayoutParams(layoutParams);
            TextView mUserName = (TextView)userInfoView.findViewById(R.id.user_info_name);
            ImageView mUserGender = (ImageView)userInfoView.findViewById(R.id.user_info_gender);
            TextView mUserInviContent = (TextView)userInfoView.findViewById(R.id.user_info_invitation_content);
            TextView mUserPosttime = (TextView)userInfoView.findViewById(R.id.user_info_posttime);
            final CircleImageView mUserAvatar = (CircleImageView)userInfoView.findViewById(R.id.user_info_avatar);
            ButtonRectangle mUserChat = (ButtonRectangle)userInfoView.findViewById(R.id.user_info_chat_button);
            mUserName.setText(name);
            mUserPosttime.setText(diffDate);
            if(gender.equals("m")){
                mUserGender.setImageResource(R.mipmap.ic_m);
//                mUserAvatar.setImageDrawable(context.getResources().getDrawable(R.drawable.avatar_default_m));
            }else if (gender.equals("f")){
                mUserGender.setImageResource(R.mipmap.ic_w);
//                mUserAvatar.setImageDrawable(context.getResources().getDrawable(R.drawable.avatar_default_f));
            }
            if(TextUtils.isEmpty(info)){
                mUserInviContent.setVisibility(View.GONE);
            }else{
                mUserInviContent.setText(info);
            }

            /*
            *       load user avatar
            * */
            ImageLoader.ImageListener userAvatorListener = ImageLoader.getImageListener(mUserAvatar,
                    R.drawable.avatar_default_m, R.drawable.avatar_default_m);
            String ServerImgUrl = Configuration.SERVER_IMG_CACHE_DIR  + name + ".png";
            if(Mode.DEBUG){
                Log.d(TAG, "-->ServerImgUrl of "+name + " is : "+ServerImgUrl);
            }
            ImageCacheManager.getInstance().getImage(ServerImgUrl,userAvatorListener);
             mUserChat.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), ChatActivity.class);
                    intent.putExtra("name",name);
                    startActivity(intent);
                }
            });
            PopupWindow popupWindow = new PopupWindow(userInfoView,(int)getResources().getDimension(R.dimen.date_fragment_popup_window_width),(int)getResources().getDimension(R.dimen.date_fragment_popup_window_height),true);
            //通过设置背景图片可以使popup window出现后能够通过点击旁白或者back键让它消失
            //popupwindow背景设置成为透明色，以防设圆角时显示黑色
            popupWindow.setBackgroundDrawable(new PaintDrawable(Color.TRANSPARENT) );
            popupWindow.setTouchable(true);
            popupWindow.setOutsideTouchable(true);
            popupWindow.showAtLocation(getView(), Gravity.CENTER_HORIZONTAL,0,0);
//            Toast.makeText(getActivity(),"'"+info+"'"+" by "+name,Toast.LENGTH_SHORT).show();
            return true;
        }
    };


    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onDatingFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnDatingFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnDatingFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onDatingFragmentInteraction(Uri uri);
    }

}
