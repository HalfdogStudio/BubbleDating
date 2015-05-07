package halfdog.bupt.edu.bubbledating.activity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.ContactsContract;
import android.provider.SyncStateContract;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import com.gc.materialdesign.views.ButtonFlat;
import com.gc.materialdesign.views.ButtonRectangle;

import java.security.AlgorithmParameterGenerator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import halfdog.bupt.edu.bubbledating.R;
import halfdog.bupt.edu.bubbledating.adapter.ChatMsgAdapter;
import halfdog.bupt.edu.bubbledating.constants.Mode;
import halfdog.bupt.edu.bubbledating.constants.Offline;
import halfdog.bupt.edu.bubbledating.db.MySQLiteOpenHelper;
import halfdog.bupt.edu.bubbledating.entity.BubbleDatingApplication;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        initViews();
        initData();
        initListeners();
    }

    public void initViews(){
        mSendMsg = (ButtonRectangle)findViewById(R.id.chat_send_msg_button);
        mInputContent = (EditText)findViewById(R.id.chat_input_content);
        mListView = (ListView)findViewById(R.id.chat_conversation_list);

    }

    public void initData(){
        Intent intent = getIntent();
        chatter = intent.getStringExtra("name");
        mDataArray = DataCache.mUserMsgList.get(chatter);
        if(mDataArray != null){
            adapter = new ChatMsgAdapter(this,mDataArray);
            mListView.setAdapter(adapter);
            mListView.setSelection(adapter.getCount() - 1);
        }

    }

    public void initListeners(){
        mSendMsg.setOnClickListener(mClickListener);
    }

    View.OnClickListener mClickListener = new View.OnClickListener(){

        @Override
        public void onClick(View v) {
            switch(v.getId()){
                case R.id.chat_send_msg_button:
                    String content = mInputContent.getText().toString();
                    if(!TextUtils.isEmpty(content)){
                        ChatMsgEntity entity = new ChatMsgEntity(chatter,content, MyDate.getCurSimpleDateFormate(),false);
                        mDataArray.add(entity);
                        adapter.refreshData(mDataArray);
                        mInputContent.setText("");
                        mListView.setSelection(mListView.getCount()-1);
                    }
                    break;
            }
        }
    };


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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
