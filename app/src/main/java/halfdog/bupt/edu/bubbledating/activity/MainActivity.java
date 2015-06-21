package halfdog.bupt.edu.bubbledating.activity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.os.PersistableBundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.easemob.EMCallBack;
import com.easemob.chat.EMChatManager;

import halfdog.bupt.edu.bubbledating.R;
import halfdog.bupt.edu.bubbledating.adapter.LeftDrawerListAdapter;
import halfdog.bupt.edu.bubbledating.constants.Mode;
import halfdog.bupt.edu.bubbledating.constants.Offline;
import halfdog.bupt.edu.bubbledating.db.MySQLiteOpenHelper;
import halfdog.bupt.edu.bubbledating.BubbleDatingApplication;
import halfdog.bupt.edu.bubbledating.fragment.dummy.DateFragment;
import halfdog.bupt.edu.bubbledating.fragment.dummy.MessageFragment;
import halfdog.bupt.edu.bubbledating.fragment.dummy.SwimDailyFragment;
import halfdog.bupt.edu.bubbledating.service.BackgroundService;
import halfdog.bupt.edu.bubbledating.tool.DataCache;
import halfdog.bupt.edu.bubbledating.tool.HXTool.HXSDKHelper;
import halfdog.bupt.edu.bubbledating.tool.NetworkStatusTool;


public class MainActivity extends ActionBarActivity implements DateFragment.OnDatingFragmentInteractionListener,
        MessageFragment.OnMessageFragmentInteractionListener,SwimDailyFragment.OnSwimDailyFragmentInteractionListener{

    public static final String TAG = "MainActivity";


    private LinearLayout dateContainer,messageContainer,swimDailyContainer;
    private ImageView dateImage,messageImage,swimDailyImage;
    private TextView dateText,messageText,swimDailyText;
    private FrameLayout fragmentContainer;
    private Fragment dateFragment,messageFragment,swimDailyFragment,currentFragment;

    private DrawerLayout drawerlayout;
    private ListView leftDrawerList;
    private ImageView leftDrawerUserAvator;
    private  TextView leftDrawerUserSignature;
    private TextView leftDrawerUserName;
    private ActionBarDrawerToggle drawerToggle;
    private Toolbar mToolbar;
    private Menu mMenu;

    private final int MENU_ADD_ID = R.id.action_add;

    public Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        SDKInitializer.initialize(getApplicationContext());
//        deleteDatabase("offline.db");
        initUI();
        initTabs();
        initListeners();
        initMeasure();

//        initOfflineData();

        initDataCache(this);
        initService(this);
        if(BubbleDatingApplication.mode != Mode.OFFLINE_MODE){
            initHXLogin();
        }




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

        drawerlayout = (DrawerLayout)findViewById(R.id.drawer_layout);
//        leftDrawerUserAvator = (ImageView)findViewById(R.id.left_drawer_user_avatar);
//        leftDrawerUserName = (TextView)findViewById(R.id.left_drawer_user_name);
        leftDrawerList = (ListView)findViewById(R.id.left_drawer_list);
//        leftDrawerUserSignature = (TextView)findViewById(R.id.left_drawer_user_signature);
        mToolbar = (Toolbar)findViewById(R.id.toolbar);



        drawerToggle = new ActionBarDrawerToggle(this,drawerlayout,mToolbar,R.string.drawer_open, R.string.drawer_close);
        drawerlayout.setDrawerListener(drawerToggle);

        drawerToggle.syncState();

        setSupportActionBar(mToolbar);
//        leftDrawerUserName.setText(BubbleDatingApplication.userEntity.getmName());
        leftDrawerList.setAdapter(new LeftDrawerListAdapter(this));
        leftDrawerList.setOnItemClickListener(leftDrawerItemClickListener);
        //用户签名
//        leftDrawerUserSignature.setText(BubbleDatingApplication.userEntity.get);


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
            dateImage.setImageDrawable(getResources().getDrawable(R.mipmap.swim_chozen));
            dateText.setTextColor(getResources().getColor(R.color.main_activity_bottom_tab_selected));

            messageImage.setImageDrawable(getResources().getDrawable(R.mipmap.message_not_chozen));
            swimDailyImage.setImageDrawable(getResources().getDrawable(R.mipmap.swim_not_chozen));

            mToolbar.setTitle("约游");

        }

    }

    public void initListeners(){
        dateContainer.setOnClickListener(onClickListener);
        messageContainer.setOnClickListener(onClickListener);
        swimDailyContainer.setOnClickListener(onClickListener);

        drawerlayout.setDrawerListener(drawerToggle);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setHomeButtonEnabled(true);
    }

    public void initMeasure(){
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        BubbleDatingApplication.screenWidth = metrics.widthPixels;
        BubbleDatingApplication.screenHeight = metrics.heightPixels;
        BubbleDatingApplication.density = metrics.density;
        BubbleDatingApplication.densityDpi = metrics.densityDpi;
    }

    public void initOfflineData(){
        MySQLiteOpenHelper instance = MySQLiteOpenHelper.getInstance(this, Offline.OFFLINE_DB);
        SQLiteDatabase db = instance.getReadableDatabase();
        db.execSQL("insert into contact_list values(null,?,?,?)",new String[]{"joseph","OK","2015-06-01 21:40:00"});
        db.execSQL("insert into contact_list values(null,?,?,?)",new String[]{"loly","不见不散","2015-05-29 8:19:47"});
        db.execSQL("insert into contact_msg_list values(null,?,?,?,?)", new String[]{"joseph", "2015-06-01 21:40:00", "Hi,约么？", "false"});
        db.execSQL("insert into contact_msg_list values(null,?,?,?,?)", new String[]{"joseph", "2015-06-01 21:41:05", "When?", "true"});
        db.execSQL("insert into contact_msg_list values(null,?,?,?,?)", new String[]{"joseph", "2015-06-01 21:43:32", "今晚9点，游泳馆门口见", "false"});
        db.execSQL("insert into contact_msg_list values(null,?,?,?,?)", new String[]{"joseph", "2015-06-01 21:45:47", "OK", "true"});
        db.execSQL("insert into contact_msg_list values(null,?,?,?,?)", new String[]{"loly", "2015-05-29 8:19:47", "晚上去游泳么", "true"});
        db.execSQL("insert into contact_msg_list values(null,?,?,?,?)", new String[]{"loly", "2015-05-29 8:19:55", "今天晚上有个会，改天吧", "false"});
        db.execSQL("insert into contact_msg_list values(null,?,?,?,?)", new String[]{"loly", "2015-05-29 8:22:21", "不见不散", "true"});
        Log.d(TAG, "-->导入离线数据成功");
    }


    public void initDataCache(Context context){
        /* init MySQLiteOpenHelper singleton and cache data */
        if(BubbleDatingApplication.mode == Mode.OFFLINE_MODE){
            MySQLiteOpenHelper.getInstance(this,Offline.OFFLINE_DB);
            DataCache.initOfflineCacheData(context);
        }else{
            deleteDatabase(BubbleDatingApplication.userEntity.getmName()+".db");
            MySQLiteOpenHelper.getInstance(this,BubbleDatingApplication.userEntity.getmName()+".db");
            DataCache.initCacheData(context);
        }
    }

    public void initHXLogin(){
        EMChatManager.getInstance().login(BubbleDatingApplication.userEntity.getmName(),
                BubbleDatingApplication.userEntity.getmPw(), new EMCallBack() {
                    @Override
                    public void onSuccess() {
                        /* Toast.maketext should be called within UI thread. */
                        Log.d(TAG, "--> LOGIN HX success");
                    }

                    @Override
                    public void onError(int i, String s) {
                        Log.e(TAG, "-->login HX failed");
                    }

                    @Override
                    public void onProgress(int i, String s) {

                    }
                });
    }

    public void initService(Context context){
        /* start network state monitoring service */
        Intent startMonitorNetworkState = new Intent(context, BackgroundService.class);
        context.startService(startMonitorNetworkState);
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
            dateFragment = (Fragment)new DateFragment();
        }
        addOrShowFragment(dateFragment);
        dateImage.setImageResource(R.mipmap.swim_chozen);
        dateText.setTextColor(getResources().getColor(R.color.main_activity_bottom_tab_selected));
        messageImage.setImageResource(R.mipmap.message_not_chozen);
        messageText.setTextColor(getResources().getColor(R.color.main_activity_bottom_tab_not_selected));
        swimDailyImage.setImageResource(R.mipmap.daily_not_chozen);
        swimDailyText.setTextColor(getResources().getColor(R.color.main_activity_bottom_tab_not_selected));
        mToolbar.setTitle("约游");
        showMenuOption(MENU_ADD_ID);
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
        mToolbar.setTitle("消息");
        hideMenuOption(MENU_ADD_ID);
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
        mToolbar.setTitle("游泳日报");
        hideMenuOption(MENU_ADD_ID);
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
    protected void onResume() {
        super.onResume();


    }

    @Override
    protected void onStart() {
        super.onStart();
        // register this event listener when this activity enters the background
        HXSDKHelper sdkHelper =  HXSDKHelper.getInstance();
        sdkHelper.pushActivity(this);

        // register the event listener when enter the foreground
//        EMChatManager.getInstance().registerEventListener(this);
//        Log.d(TAG,"-->注册消息监听");
        if(!NetworkStatusTool.isConnected(MainActivity.this)){
            Toast.makeText(this, R.string.network_state_not_connected,Toast.LENGTH_LONG).show();
        }
    }



    @Override
    protected void onStop() {
//        EMChatManager.getInstance().unregisterEventListener(this);
//        Log.d(TAG, "-->注销消息监听");
        HXSDKHelper sdkHelper =  HXSDKHelper.getInstance();
        sdkHelper.popActivity(this);
        super.onStop();

    }

    @Override
    public void onPostCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onPostCreate(savedInstanceState, persistentState);
        drawerToggle.syncState();
        if(!NetworkStatusTool.isConnected(MainActivity.this)){
            Toast.makeText(this, R.string.network_state_not_connected,Toast.LENGTH_LONG).show();
        }
    }

    AdapterView.OnItemClickListener leftDrawerItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            switch(position){
                case 0:
                    Intent toPersonInfo = new Intent(MainActivity.this,PersonInfoActivity.class);
                    startActivity(toPersonInfo);
                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                    break;
                case 1:
                    /*  feedback  */
                    Intent toFeedback = new Intent(MainActivity.this,FeedbackActivity.class);
                    startActivity(toFeedback);
                    overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
                    break;
                case 2:
                    /* about info activity */
                    Intent toAboutInfo = new Intent(MainActivity.this,AboutInfoActivity.class);
                    startActivity(toAboutInfo);
                    overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
                    break;
            }
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        this.mMenu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch(id){
            case R.id.action_add:
                Intent toInvitationActivity = new Intent(MainActivity.this,InvitationActivity.class);
                startActivity(toInvitationActivity);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

        }

        if(drawerToggle.onOptionsItemSelected(item)){
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }


    @Override
    public void onBackPressed() {
        if(drawerlayout.isDrawerOpen(Gravity.START|Gravity.LEFT)){
            drawerlayout.closeDrawers();
            return ;
        }
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        /*关闭服务*/
        Intent shutNetworkStateMonService = new Intent(this,BackgroundService.class);
        stopService(shutNetworkStateMonService);
    }

//    @Override
//    public void onEvent(EMNotifierEvent emNotifierEvent) {
//        {
//            Log.d(TAG,"-->EMNotifierEvent:"+emNotifierEvent.getData().toString());
//            Log.d(TAG,"-->EMNotifierEvent:"+emNotifierEvent.getEvent().toString());
//
//            switch (emNotifierEvent.getEvent()) {
//                case EventNewMessage: //普通消息
//                {
//                    EMMessage message = (EMMessage) emNotifierEvent.getData();
//                    Log.d(TAG,"-->normal message:"+message.toString());
//                    //提示新消息
//                    HXSDKHelper instance = HXSDKHelper.getInstance();
//                    Log.d(TAG, "-->instance == null?:"+(instance == null));
//                    HXNotifier notifiier =  instance.getNotifier();
//                    Log.d(TAG, "-->notifiier == null?:"+(notifiier == null));
//                    Log.d(TAG,"-->notifier:"+notifiier.getClass().getName());
//                    notifiier.onNewMsg(message,this);
//
////                    refreshUI();
//                    break;
//                }
//
//                case EventOfflineMessage:
//                {
//                    Log.d(TAG,"-->get offline message");
////                    refreshUI();
//                    break;
//                }
//
//                default:
//                    Log.d(TAG,"-->get default message");
//                    break;
//            }
//        }
//    }

    @Override
    public void onDatingFragmentInteraction(Uri uri) {

    }

    @Override
    public void onMessageFragmentInteraction(Uri uri) {

    }

    @Override
    public void onSwimDailyFragmentInteraction(Uri uri) {

    }

    private void hideMenuOption(int id){
        MenuItem item = mMenu.findItem(id);
        item.setVisible(false);
    }

    private void showMenuOption(int id){
        MenuItem item = mMenu.findItem(id);
        item.setVisible(true);
    }
}
