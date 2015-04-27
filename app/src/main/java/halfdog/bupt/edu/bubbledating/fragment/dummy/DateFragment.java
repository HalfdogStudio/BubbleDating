package halfdog.bupt.edu.bubbledating.fragment.dummy;

import android.app.Activity;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMapOptions;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.SupportMapFragment;
import com.baidu.mapapi.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.transform.ErrorListener;

import halfdog.bupt.edu.bubbledating.R;
import halfdog.bupt.edu.bubbledating.entity.BubbleDatingApplication;
import halfdog.bupt.edu.bubbledating.tool.ImageMerger;

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
    public final String REQUEST_PEOPLE_AROUND = "http://10.108.245.37:8080/BubbleDatingServer/HandlePeopleAround";
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    public final String TAG = "DateFragment";

    private MapView mMapView;
    private BaiduMap mMap;
    private LocationClient mLocationClient = null;
    public BDLocationListener listener = new MyLocationListener();

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
        mMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                switch (marker.getTitle()){
                    case "1":
                        Bundle extra  = marker.getExtraInfo();
                        String info = extra.getString("hobby");
                        Toast.makeText(getActivity(),"andy's hobby is"+info,Toast.LENGTH_SHORT).show();
                        break;
                }
                return true;
            }
        });
        MapStatusUpdate update = MapStatusUpdateFactory.newLatLngZoom(BubbleDatingApplication.userLatLng,17);
        mMap.setMapStatus(update);
        /*
        *       request to get people around
        * */

        RequestQueue queue  = Volley.newRequestQueue(getActivity());
        JsonArrayRequest requestPeopleAround = new JsonArrayRequest(Request.Method.GET,REQUEST_PEOPLE_AROUND,responseListener,errorListener);
        queue.add(requestPeopleAround);


        Bitmap photoOfHead = ImageMerger.addTextOnBitmap("andy",R.mipmap.ic_locator_m_2,getActivity());
        BitmapDescriptor bitmap = BitmapDescriptorFactory.fromBitmap(photoOfHead);
        Bundle extraInfo = new Bundle();
        extraInfo.putString("hobby","swimming");
        OverlayOptions options = new MarkerOptions().title("1").position(BubbleDatingApplication.userLatLng).icon(bitmap).extraInfo(extraInfo);
        mMap.addOverlay(options);
        return view;



    }

    Response.Listener<JSONArray> responseListener = new Response.Listener<JSONArray>() {
        @Override
        public void onResponse(JSONArray jsonArray) {
            Log.d(TAG,"--> get reponse with json array from server ");
            for(int i = 0; i < jsonArray.length(); i ++ ){
                try {
                    JSONObject item = jsonArray.getJSONObject(i);
                    int uId = item.getInt("u_id");
                    String uName = item.getString("u_name");
                    String uInviName = item.getString("u_invi");
                    String uGender = item.getString("u_gender");
                    double uLat = item.getDouble("u_loc_lat");
                    double uLong = item.getDouble("u_loc_long");
                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date uDate = df.parse(item.getString("u_posttime"));
                    Log.d(TAG,"-->uId:"+uId+"  uName:"+uName+" uInviName:"+uInviName+" uGender:"+uGender+
                    " uLat:"+uLat+" uLong:"+uLong+" uDate:"+uDate.toString());
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

    public class MyLocationListener implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            if (location == null)
                return ;
            StringBuffer sb = new StringBuffer(256);
            sb.append("time : ");
            sb.append(location.getTime());
            sb.append("\nerror code : ");
            sb.append(location.getLocType());
            sb.append("\nlatitude : ");
            sb.append(location.getLatitude());
            sb.append("\nlontitude : ");
            sb.append(location.getLongitude());
            sb.append("\nradius : ");
            sb.append(location.getRadius());
            if (location.getLocType() == BDLocation.TypeGpsLocation){
                sb.append("\nspeed : ");
                sb.append(location.getSpeed());
                sb.append("\nsatellite : ");
                sb.append(location.getSatelliteNumber());
            } else if (location.getLocType() == BDLocation.TypeNetWorkLocation){
                sb.append("\naddr : ");
                sb.append(location.getAddrStr());
            }

            Log.d("", "-->" + sb.toString());

            LatLng point  = new LatLng(location.getLatitude(),location.getLongitude());
            BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.mipmap.ic_locator_m_2);
            OverlayOptions options = new MarkerOptions().position(point).icon(bitmap);
            mMap.addOverlay(options);
            mLocationClient.stop();
            Log.d(TAG,"-->停止定位");
        }


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
