package halfdog.bupt.edu.bubbledating.activity;

import android.app.DownloadManager;
import android.content.DialogInterface;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.baidu.navisdk.ui.widget.NewerGuideDialog;
import com.gc.materialdesign.views.ButtonRectangle;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.math.MathContext;
import java.util.HashMap;
import java.util.Map;

import halfdog.bupt.edu.bubbledating.BubbleDatingApplication;
import halfdog.bupt.edu.bubbledating.R;
import halfdog.bupt.edu.bubbledating.constants.Configuration;
import halfdog.bupt.edu.bubbledating.constants.Mode;
import halfdog.bupt.edu.bubbledating.entity.UserEntity;
import halfdog.bupt.edu.bubbledating.tool.CustomRequest;
import halfdog.bupt.edu.bubbledating.tool.RequestManager;

public class FeedbackActivity extends ActionBarActivity {

    private final String TAG = "FeedbackActivity";

    private EditText mContent;
    private ButtonRectangle mSubmit;
    private ButtonRectangle mCancel;
    private static com.gc.materialdesign.widgets.ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        mContent = (EditText)findViewById(R.id.feedback_activity_edittext);
        mSubmit = (ButtonRectangle)findViewById(R.id.feedback_activity_submit);
        mCancel = (ButtonRectangle)findViewById(R.id.feedback_activity_cancel);

        mSubmit.setOnClickListener(onClickListener);
        mCancel.setOnClickListener(onClickListener);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        progressDialog = new com.gc.materialdesign.widgets.ProgressDialog(FeedbackActivity.this,
                FeedbackActivity.this.getResources().getString(R.string.progress_bar_hint));
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch(v.getId()){
                case R.id.feedback_activity_submit:
                    String content = mContent.getText().toString();
                    if(TextUtils.isEmpty(content)){
                        Toast.makeText(FeedbackActivity.this,"反馈内容为空",Toast.LENGTH_LONG).show();
                    }else{
                        Map<String,String> data = new HashMap<>();
                        data.put("data",content);
                        data.put("username", BubbleDatingApplication.userEntity.getmName());
                        if(Mode.DEBUG){
                            Log.d(TAG,"-->data;"+content+",username:"+ BubbleDatingApplication.userEntity.getmName());
                            Log.d(TAG,"--> target url:"+Configuration.FEED_BACK_REQUEST);
                        }
                        CustomRequest request = new CustomRequest(Request.Method.POST, Configuration.FEED_BACK_REQUEST,
                                data,okListener,errorListener);
                        RequestManager.getInstance(FeedbackActivity.this).add(request);
                        progressDialog.show();
                    }
                    break;
                case R.id.feedback_activity_cancel:
                    FeedbackActivity.this.finish();
                    overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
                    break;
            }
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_feedback, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch(id){
            case android.R.id.home:
                FeedbackActivity.this.finish();
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                return true;
            case R.id.action_settings:
                return true;
        }

        //noinspection SimplifiableIfStatement

        return super.onOptionsItemSelected(item);
    }

    Response.Listener<JSONObject> okListener = new Response.Listener<JSONObject>() {
        @Override
        public void onResponse(JSONObject response) {
            progressDialog.dismiss();
            try {
                int status = response.getInt("result");
                if(status == 1){
                    Toast.makeText(FeedbackActivity.this,"提交成功",Toast.LENGTH_LONG).show();
                    FeedbackActivity.this.finish();
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }else{
                    Toast.makeText(FeedbackActivity.this,"提交失败，请稍后重试",Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(FeedbackActivity.this,"解析响应发生异常，请稍后重试",Toast.LENGTH_LONG).show();
            }


        }
    };

    Response.ErrorListener errorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            progressDialog.dismiss();
            Toast.makeText(FeedbackActivity.this,"发生未知错误，请稍后重试",Toast.LENGTH_LONG).show();
        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_BACK){
            this.finish();
            overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
