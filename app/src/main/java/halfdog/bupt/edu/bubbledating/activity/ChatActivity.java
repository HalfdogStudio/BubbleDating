package halfdog.bupt.edu.bubbledating.activity;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import com.easemob.EMCallBack;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMMessage;
import com.easemob.chat.TextMessageBody;
import com.gc.materialdesign.views.ButtonRectangle;

import java.util.ArrayList;
import java.util.List;

import halfdog.bupt.edu.bubbledating.BubbleDatingApplication;
import halfdog.bupt.edu.bubbledating.R;
import halfdog.bupt.edu.bubbledating.adapter.ChatMsgAdapter;
import halfdog.bupt.edu.bubbledating.constants.Configuration;
import halfdog.bupt.edu.bubbledating.constants.Mode;
import halfdog.bupt.edu.bubbledating.db.MySQLiteOpenHelper;
import halfdog.bupt.edu.bubbledating.entity.ChatMsgEntity;
import halfdog.bupt.edu.bubbledating.tool.DataCache;
import halfdog.bupt.edu.bubbledating.tool.MyDate;

public class ChatActivity extends ActionBarActivity {
    public static final String TAG = "ChatActivity";
    private ButtonRectangle mSendMsg;
    private static EditText mInputContent;
    private static ListView mListView;
    private static List<ChatMsgEntity> mDataArray;
    public static String chatter;
    private static ChatMsgAdapter adapter;
    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        initViews();
        initData();
        initListeners();


    }

    @Override
    protected void onStart() {
        super.onStart();

        //  update  data
        mDataArray = DataCache.mUserMsgList.get(chatter);
        if (mDataArray != null) {
            adapter = new ChatMsgAdapter(this, mDataArray);
            mListView.setAdapter(adapter);
            mListView.setSelection(adapter.getCount() - 1);
        } else {
            mDataArray = new ArrayList<>();
            adapter = new ChatMsgAdapter(this, mDataArray);
            mListView.setAdapter(adapter);
        }

        db = MySQLiteOpenHelper.getInstance(this).getWritableDatabase();

//        ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
//        ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
//        Log.d("", "-->pkg:" + cn.getPackageName());
//        Log.d("", "-->cls:" + cn.getClassName());

        BubbleDatingApplication.setCurrentActivity(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        BubbleDatingApplication.setCurrentActivity(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        clearReferences();
    }

    @Override
    protected void onStop() {
        super.onStop();
        clearReferences();
        if (db != null) {
            db.close();
        }
    }

    private void clearReferences() {
        Activity currActivity = BubbleDatingApplication.getCurrentActivity();
        if (currActivity != null && currActivity.equals(this))
            BubbleDatingApplication.setCurrentActivity(null);
    }


    public void initViews() {
        mSendMsg = (ButtonRectangle) findViewById(R.id.chat_send_msg_button);
        mInputContent = (EditText) findViewById(R.id.chat_input_content);
        mListView = (ListView) findViewById(R.id.chat_conversation_list);

    }

    public void initData() {
        Intent intent = getIntent();
        chatter = intent.getStringExtra("name");

        // set home icon as "<--" back button
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(chatter);


    }

    public void initListeners() {
        mSendMsg.setOnClickListener(mClickListener);
    }

    public static void sendOrReceiveUiMsg(ChatMsgEntity entity, boolean isReceive) throws IllegalStateException{
        String chatter = null;
        if(isReceive){
            chatter = entity.getmFrom();
        }else{
            chatter = entity.getTo();
        }
        Log.d(TAG,"-->before get new msg, mDataArray.size():"+mDataArray.size());
//        mDataArray.add(entity);
        /* mDataArray 应该保持与 mUserMsgList 保持数据一致 */
        if(DataCache.mUserMsgList.isEmpty()){
            throw new IllegalStateException("mUserMsgList is null, cannot get mDataArray ");
        }
        mDataArray = DataCache.mUserMsgList.get(chatter);
        Log.d(TAG, "-->ater get new msg, mDataArray.size():" + mDataArray.size());
        adapter.refreshData(mDataArray);
        mListView.setSelection(mListView.getCount() - 1);
        if (!isReceive) {
            mInputContent.setText("");
        }

    }

    public static void refreshListView() {
        mListView.invalidateViews();
    }

    View.OnClickListener mClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.chat_send_msg_button:
                    if(!mDataArray.isEmpty()){
                        Log.d(TAG,"-->Before send operation, mDataArray has "+mDataArray.size() + " elements");
                    }
                    String content = mInputContent.getText().toString();
                    if (!TextUtils.isEmpty(content)) {
                        ChatMsgEntity entity = new ChatMsgEntity(chatter,BubbleDatingApplication.userEntity.getmName(),  content, MyDate.getCurSimpleDateFormate(), false );
                        /* sqlite operation and update DataCache.mContactUser and DataCache.mUserMsgList    */
                        DataCache.updateUsrMsgAndContacListInMemory(entity);

                        sendOrReceiveUiMsg(entity, false);
                        DataCache.updateUsrMsgAndContactListInDB(entity, db);

                        if (BubbleDatingApplication.mode != Mode.OFFLINE_MODE) {
                        /*  use HX Tool to send message */
                            sendHXMsg(chatter, content);
                        }
                    }
                    if(!mDataArray.isEmpty()){
                        Log.d(TAG,"-->After send operation, mDataArray has "+mDataArray.size() + " elements");
                    }
                    break;
            }
        }
    };


    public void sendHXMsg(String username, String content) {
        //获取到与聊天人的会话对象。参数username为聊天人的userid或者groupid，后文中的username皆是如此
        EMConversation conversation = EMChatManager.getInstance().getConversation(username);
        //创建一条文本消息
        EMMessage message = EMMessage.createSendMessage(EMMessage.Type.TXT);
        //如果是群聊，设置chattype,默认是单聊
//        message.setChatType(EMMessage.ChatType.GroupChat);
        //设置消息body
        TextMessageBody txtBody = new TextMessageBody(content);
        message.addBody(txtBody);
        //设置接收人
        message.setReceipt(username);
        //把消息加入到此会话对象中
        conversation.addMessage(message);
        //发送消息
        EMChatManager.getInstance().sendMessage(message, new EMCallBack() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "-->MSG SEND success");
            }

            @Override
            public void onError(int i, String s) {
                Log.d(TAG, "-->MSG SEND failed");
            }

            @Override
            public void onProgress(int i, String s) {

            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_chat, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                this.finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                return true;
            case R.id.action_settings:
                return true;
        }


        return super.onOptionsItemSelected(item);
    }

    static public Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case Configuration.UPDATE_CHAT_ACTIVITY_CONTACT:
                    ChatMsgEntity entity = (ChatMsgEntity) msg.obj;
                    Log.d("", "-->entity from notifier:" + entity.toString());
                    ChatActivity.sendOrReceiveUiMsg(entity, true);
                    break;
            }
        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_BACK) {
            this.finish();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


}
