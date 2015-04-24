package halfdog.bupt.edu.bubbledating.activity;

import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import halfdog.bupt.edu.bubbledating.R;
import halfdog.bupt.edu.bubbledating.fragment.dummy.DateFragment;
import halfdog.bupt.edu.bubbledating.fragment.dummy.MessageFragment;
import halfdog.bupt.edu.bubbledating.fragment.dummy.SwimDailyFragment;


public class MainActivity extends ActionBarActivity implements DateFragment.OnDatingFragmentInteractionListener,
        SwimDailyFragment.OnSwimDailyFragmentInteractionListener,MessageFragment.OnMessageFragmentInteractionListener {
    public static final String TAG = "MainActivity";

    private LinearLayout dateContainer,messageContainer,swimDailyContainer;
    private ImageView dateImage,messageImage,swimDailyImage;
    private TextView dateText,messageText,swimDailyText;
    private FrameLayout fragmentContainer;
    private Fragment dateFragment,messageFragment,swimDailyFragment,currentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initUI();
        initTabs();
        initListeners();

    }

    public void initUI(){
        dateContainer = (LinearLayout)findViewById(R.id.date_container);
        messageContainer = (LinearLayout)findViewById(R.id.message_container);
        swimDailyContainer = (LinearLayout)findViewById(R.id.swim_daily_container);

        dateImage = (ImageView)findViewById(R.id.date_icon);
        messageImage = (ImageView)findViewById(R.id.message_icon);
        swimDailyImage = (ImageView)findViewById(R.id.swim_daily_icon);

        dateText = (TextView)findViewById(R.id.date_text);
        messageText = (TextView)findViewById(R.id.message_text);
        swimDailyText = (TextView)findViewById(R.id.swim_daily_text);

    }

    public void initTabs(){
        if(dateFragment == null){
            dateFragment = new DateFragment();
            Log.d(TAG,"-->ADD DATING FRAGMENT");
        }

        if(!dateFragment.isAdded()){
            Log.d(TAG,"--> DATING FRAGMENT IS NOT ADDED");
            getSupportFragmentManager().beginTransaction().add(R.id.main_activity_fragment_container,dateFragment).commit();
            currentFragment = dateFragment;
            dateImage.setImageResource(R.mipmap.swim_chozen);
            dateText.setTextColor(getResources().getColor(R.color.main_activity_bottom_tab_selected));
        }

    }

    public void initListeners(){
        dateContainer.setOnClickListener(onClickListener);
        messageContainer.setOnClickListener(onClickListener);
        swimDailyContainer.setOnClickListener(onClickListener);
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.date_container:
                    clickDateContainer();
                    break;
                case R.id.message_container:
                    clickMessageContainer();
                    break;
                case R.id.swim_daily_container:
                    clickSwimDailyContainer();
                    break;

            }
        }
    };

    public void clickDateContainer(){
        if(dateFragment == null){
            dateFragment = new DateFragment();
        }
        addOrShowFragment(dateFragment);
        dateImage.setImageResource(R.mipmap.swim_chozen);
        dateText.setTextColor(getResources().getColor(R.color.main_activity_bottom_tab_selected));
        messageImage.setImageResource(R.mipmap.message_not_chozen);
        messageText.setTextColor(getResources().getColor(R.color.main_activity_bottom_tab_not_selected));
        swimDailyImage.setImageResource(R.mipmap.daily_not_chozen);
        swimDailyText.setTextColor(getResources().getColor(R.color.main_activity_bottom_tab_not_selected));
    }

    public void clickMessageContainer(){
        if(messageFragment == null){
            messageFragment = new MessageFragment();
        }
        addOrShowFragment(messageFragment);
        dateImage.setImageResource(R.mipmap.swim_not_chozen);
        dateText.setTextColor(getResources().getColor(R.color.main_activity_bottom_tab_not_selected));
        messageImage.setImageResource(R.mipmap.message_chozen);
        messageText.setTextColor(getResources().getColor(R.color.main_activity_bottom_tab_selected));
        swimDailyImage.setImageResource(R.mipmap.daily_not_chozen);
        swimDailyText.setTextColor(getResources().getColor(R.color.main_activity_bottom_tab_not_selected));
    }

    public void clickSwimDailyContainer(){
        if(swimDailyFragment == null){
            swimDailyFragment = new SwimDailyFragment();
        }
        addOrShowFragment(swimDailyFragment);
        dateImage.setImageResource(R.mipmap.swim_not_chozen);
        dateText.setTextColor(getResources().getColor(R.color.main_activity_bottom_tab_not_selected));
        messageImage.setImageResource(R.mipmap.message_not_chozen);
        messageText.setTextColor(getResources().getColor(R.color.main_activity_bottom_tab_not_selected));
        swimDailyImage.setImageResource(R.mipmap.daily_chozen);
        swimDailyText.setTextColor(getResources().getColor(R.color.main_activity_bottom_tab_selected));
    }

    private void addOrShowFragment( Fragment fragment){
        if(currentFragment == fragment) return ;

        if(!fragment.isAdded()){
            getSupportFragmentManager().beginTransaction().hide(currentFragment).add(R.id.main_activity_fragment_container,fragment).commit();
        }else{
            getSupportFragmentManager().beginTransaction().hide(currentFragment).show(fragment).commit();
        }

        currentFragment = fragment;

    }





    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDatingFragmentInteraction(Uri uri) {

    }

    @Override
    public void onMessageFragmentInteraction(Uri uri) {

    }

    @Override
    public void onSwimDailyFragmentInteraction(Uri uri) {

    }
}
