package halfdog.bupt.edu.bubbledating.fragment.dummy;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import halfdog.bupt.edu.bubbledating.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SwimDailyFragment.OnSwimDailyFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SwimDailyFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SwimDailyFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private final String SWIM_DAILY_URL = "http://reverland.org/topswim/toc.html";

    private WebView webViewDaily;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnSwimDailyFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SwimDailyFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SwimDailyFragment newInstance(String param1, String param2) {
        SwimDailyFragment fragment = new SwimDailyFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public SwimDailyFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_swim_daily, container, false);
        webViewDaily = (WebView)view.findViewById(R.id.webview_daily);
        webViewDaily.getSettings().setJavaScriptEnabled(true);
        webViewDaily.getSettings().setBuiltInZoomControls(false);
        webViewDaily.getSettings().setTextZoom(160);

        //设置自适应手机屏幕
        webViewDaily.getSettings().setUseWideViewPort(true);
        webViewDaily.getSettings().setLoadWithOverviewMode(true);

        webViewDaily.loadUrl(SWIM_DAILY_URL);

        /*try to remove side blanks*/
        webViewDaily.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);

        final Activity activity = this.getActivity();
        webViewDaily.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                activity.setProgress(1000 * newProgress);
            }
        });

        webViewDaily.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url); // 使用webview处理跳转
                return true; // 表示此事件已被处理，不再继续广播
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                Toast.makeText(getActivity(),"Oh no!"+description,Toast.LENGTH_SHORT).show();
            }
        });

        webViewDaily.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((keyCode == KeyEvent.KEYCODE_BACK) && webViewDaily.canGoBack()) {
                    webViewDaily.goBack();
                    return true;
                }
                return false;
            }
        });
        return view;
    }



    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onSwimDailyFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnSwimDailyFragmentInteractionListener) activity;
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
    public interface OnSwimDailyFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onSwimDailyFragmentInteraction(Uri uri);
    }

}
