package halfdog.bupt.edu.bubbledating.activity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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
import halfdog.bupt.edu.bubbledating.constants.Mode;
import halfdog.bupt.edu.bubbledating.db.MySQLiteOpenHelper;
import halfdog.bupt.edu.bubbledating.entity.ChatMsgEntity;
import halfdog.bupt.edu.bubbledating.tool.DataCache;
import halfdog.bupt.edu.bubbledating.tool.MyDate;

public class ChatActivity extends ActionBarActivity {
    public final String TAG = "ChatActivity";
    private ButtonRectangle mSendMsg;
    private EditText mInputContent;
    private ListView mListView;
    private List<ChatMsgEntity> mDataArray;
    private static String chatter;
    private ChatMsgAdapter adapter;
    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        initViews();
        initData();
        initListeners();

    }

    public void initViews() {
        mSendMsg = (ButtonRectangle) findViewById(R.id.chat_send_msg_button);
        mInputContent = (EditText) findViewById(R.id.chat_input_content);
        mListView = (ListView) findViewById(R.id.chat_conversation_list);

    }

    public void initData() {
        Intent intent = getIntent();
        chatter = intent.getStringExtra("name");
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
        // set home icon as "<--" back button
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(chatter);

        db = MySQLiteOpenHelper.getInstance(this).getWritableDatabase();


    }

    public void initListeners() {
        mSendMsg.setOnClickListener(mClickListener);
    }

    View.OnClickListener mClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.chat_send_msg_button:
                    String content = mInputContent.getText().toString();
                    if (!TextUtils.isEmpty(content)) {
                        ChatMsgEntity entity = new ChatMsgEntity(chatter, content, MyDate.getCurSimpleDateFormate(), false);
                        mDataArray.add(entity);
                        adapter.refreshData(mDataArray);
                        mInputContent.setText("");
                        mListView.setSelection(mListView.getCount() - 1);


                        /* sqlite operation and update DataCache.mContactUser and DataCache.mUserMsgList    */
                        if (BubbleDatingApplication.mode != Mode.OFFLINE_MODE) {
                        /* insert into contact_msg_list */
                            String insertSql = "insert into " + MySQLiteOpenHelper.CONTACT_MSG_TABLE_NAME +
                                    " (name,date,content,is_receive) values ('" + entity.getName() + "','" + entity.getDate() +
                                    "','" + entity.getContent() + "','" + false + "')";
                            Log.d(TAG, "-->INSERT SQL:" + insertSql);
                            db.execSQL(insertSql);


                            if (DataCache.mUserMsgList.containsKey(entity.getName())) {
                                DataCache.mUserMsgList.get(entity.getName()).add(new ChatMsgEntity(entity.getName(), entity.getContent(),
                                        entity.getDate(), false));
                            } else {
                                List<ChatMsgEntity> list = new ArrayList<>();
                                list.add(new ChatMsgEntity(entity.getName(), entity.getContent(),
                                        entity.getDate(), false));
                                DataCache.mUserMsgList.put(entity.getName(), list);
                            }

                        /* update contact_list 最近的消息， message fragment */
                            String querySql = "select * from " + MySQLiteOpenHelper.CONTACT_TABLE_NAME +
                                    " where name='" + entity.getName() + "'";
                            Log.d(TAG, "-->query 1:" + querySql);
                            Cursor cursor = db.rawQuery(querySql, null);
                            if (cursor.getCount() == 0) {
                                String insertSql2 = "insert into " + MySQLiteOpenHelper.CONTACT_TABLE_NAME +
                                        "(name,last_message,last_contact_date) values ('" +
                                        entity.getName() + "','" + entity.getContent() + "','" + entity.getDate() + "')";
                                Log.d(TAG, "--> insert sql 2: " + insertSql2);
                                db.execSQL(insertSql2);
                                cursor.close();

                                DataCache.mContactUser.add(new ChatMsgEntity(entity.getName(), entity.getContent(),
                                        entity.getDate(), false));
                            } else {
                                String updateSql = "update " + MySQLiteOpenHelper.CONTACT_TABLE_NAME + " set last_message='" +
                                        entity.getContent() + "',last_contact_date='" + entity.getDate() + "'";
                                Log.d(TAG, "-->updateSql:" + updateSql);
                                db.execSQL(updateSql);

                                int index = 0;
                                for (; index <  DataCache.mContactUser.size(); index++) {
                                    Log.d(TAG,"-->index:"+index);
                                    if (TextUtils.equals(DataCache.mContactUser.get(index).getName(),entity.getName())){
                                        break;
                                    }
                                }
                                Log.d(TAG,"-->AFTER THE FOR LOOP, INDEX:"+index);
                                if (TextUtils.equals(DataCache.mContactUser.get(index).getName(), entity.getName())) {

                                    DataCache.mContactUser.get(index).setContent(entity.getContent());
                                    DataCache.mContactUser.get(index).setDate(entity.getDate());
                                }
                            }

                            /*  use HX Tool to send message */
                            sendHXMsg(chatter,content);

                        }


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
                Log.d(TAG,"-->MSG SEND success");
            }

            @Override
            public void onError(int i, String s) {
                Log.d(TAG,"-->MSG SEND failed");
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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_BACK){
            this.finish();
            overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


}
