package halfdog.bupt.edu.bubbledating.activity;

import android.content.Context;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import halfdog.bupt.edu.bubbledating.BubbleDatingApplication;
import halfdog.bupt.edu.bubbledating.R;
import halfdog.bupt.edu.bubbledating.constants.Configuration;
import halfdog.bupt.edu.bubbledating.tool.CustomRequest;
import halfdog.bupt.edu.bubbledating.tool.MyDate;
import halfdog.bupt.edu.bubbledating.tool.NetworkStatusTool;
import halfdog.bupt.edu.bubbledating.tool.RequestManager;

public class InvitationActivity extends ActionBarActivity {
    private EditText mContent;
    private com.gc.materialdesign.widgets.ProgressDialog mProgressDialog;
    private Context context;
    private final String TAG = "InvitationActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invitation);
        context = this;
        mContent = (EditText) findViewById(R.id.invitation_activity_content);
        mProgressDialog = new com.gc.materialdesign.widgets.ProgressDialog(InvitationActivity.this,
                getResources().getString(R.string.progress_bar_hint));

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_invitation, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.action_submit:
                String content = mContent.getText().toString();
                if (TextUtils.isEmpty(content)) {
                    Toast.makeText(this, R.string.activity_invitation_empty_content_hint, Toast.LENGTH_LONG).show();
                    return true;
                }

                if(!NetworkStatusTool.isConnected(context)){
                    Toast.makeText(context,context.getResources().getString(R.string.network_unavailabel),Toast.LENGTH_LONG).show();
                    return true;
                }
                /* post a request to insert invitation to server */
                String url = Configuration.SERVER_IP + File.separator + Configuration.SUBMIT_NEW_INVITATION;
                Log.d(TAG, "-->insert invitation url:" + url);
                Map<String, String> data = new HashMap<>();
                data.put("name", BubbleDatingApplication.userEntity.getmName());
                data.put("gender", BubbleDatingApplication.userEntity.getmGender());
                data.put("invitation", content);
                data.put("posttime", String.valueOf(MyDate.getCurrentDate().getTime()));
                data.put("lat", String.valueOf(BubbleDatingApplication.userLatLng.latitude));
                data.put("long", String.valueOf(BubbleDatingApplication.userLatLng.longitude));

                mProgressDialog.show();
                CustomRequest request = new CustomRequest(Request.Method.POST, url, data, reponseListener, errorListener);
                RetryPolicy retryPolicy = new DefaultRetryPolicy(
                        Configuration.REQUEST_TIMEOUT_MS,
                        Configuration.MAX_RETRY_TIMES,
                        Configuration.BACK_OFF_MULTI
                );
                request.setRetryPolicy(retryPolicy);
                RequestManager.getInstance(this).add(request);
                return true;
            case android.R.id.home:
                this.finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                return true;

        }

        return super.onOptionsItemSelected(item);
    }

    Response.Listener<JSONObject> reponseListener = new Response.Listener<JSONObject>() {
        @Override
        public void onResponse(JSONObject response) {
            mProgressDialog.dismiss();
            try {
                int result = response.getInt("result");
                if(result == 1){
                    Toast.makeText(InvitationActivity.this, R.string.activity_invitation_response_ok, Toast.LENGTH_LONG).show();
                    InvitationActivity.this.finish();
                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);

                }else{
                    Toast.makeText(InvitationActivity.this, R.string.activity_invitation_response_not_ok, Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(InvitationActivity.this, R.string.activity_invitation_response_not_ok, Toast.LENGTH_LONG).show();
            }

        }
    };

    Response.ErrorListener errorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            mProgressDialog.dismiss();
            Toast.makeText(InvitationActivity.this, R.string.volley_request_timeout_error, Toast.LENGTH_LONG).show();
        }
    };

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
