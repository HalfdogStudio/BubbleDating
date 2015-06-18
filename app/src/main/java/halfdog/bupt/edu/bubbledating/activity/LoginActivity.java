package halfdog.bupt.edu.bubbledating.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.gc.materialdesign.views.ButtonRectangle;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import halfdog.bupt.edu.bubbledating.R;
import halfdog.bupt.edu.bubbledating.constants.Configuration;
import halfdog.bupt.edu.bubbledating.constants.Mode;
import halfdog.bupt.edu.bubbledating.BubbleDatingApplication;
import halfdog.bupt.edu.bubbledating.entity.UserEntity;
import halfdog.bupt.edu.bubbledating.tool.CustomRequest;
import halfdog.bupt.edu.bubbledating.constants.ResponseState;
import halfdog.bupt.edu.bubbledating.constants.UserInfoKeys;
import halfdog.bupt.edu.bubbledating.tool.RequestManager;

public class LoginActivity extends Activity {
    private static final String TAG = "LoginActivity";
    private static final String LOGIN_URL = Configuration.SERVER_IP + "/BubbleDatingServer/HandleLogin";

    private com.gc.materialdesign.views.ButtonRectangle useWithoutLogin;
    private com.gc.materialdesign.views.ButtonRectangle registerButton;
    private com.gc.materialdesign.views.ButtonRectangle loginButton;

    private static EditText loginName;
    private static EditText loginPw;

    private final Context context = LoginActivity.this;
    private static com.gc.materialdesign.widgets.ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        useWithoutLogin = (com.gc.materialdesign.views.ButtonRectangle)findViewById(R.id.use_without_login);
        registerButton = (ButtonRectangle)findViewById(R.id.login_activity_register);
        loginButton = (ButtonRectangle)findViewById(R.id.login_activity_launch);
        loginName = (EditText)findViewById(R.id.login_activity_username);
        loginPw = (EditText)findViewById(R.id.login_activity_password);



        useWithoutLogin.setOnClickListener(buttonListener);
        registerButton.setOnClickListener(buttonListener);
        loginButton.setOnClickListener(buttonListener);

        progressDialog = new com.gc.materialdesign.widgets.ProgressDialog(LoginActivity.this,"请稍候");
//        context = this;
    }

    View.OnClickListener buttonListener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            switch(v.getId()){
                case R.id.use_without_login:
                    Intent toMainActivity = new Intent(LoginActivity.this,MainActivity.class);
                    //离线以 27, "aaa" , "aaa", "aaa@qq.com","f",null,true 登陆
                    BubbleDatingApplication.userEntity = new UserEntity(27,"aaa","aaa","aaa@qq.com","f",null,true);
                    BubbleDatingApplication.mode = Mode.OFFLINE_MODE;
                    startActivity(toMainActivity);
                    overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
                    finish();
                    break;
                case R.id.login_activity_launch:

                    login(LoginActivity.this);
                    break;
                case R.id.login_activity_register:
                    Intent toRegisterActivity = new Intent(LoginActivity.this,RegisterAccount.class);
                    startActivity(toRegisterActivity);
                    overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
                    break;

            }

        }
    };




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
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


    Response.Listener<JSONObject> reponseListener = new Response.Listener<JSONObject>() {
        @Override
        public void onResponse(JSONObject response) {
            {
                Log.d(TAG, "-->log in response:" + response.toString());
                try {
                    int status = response.getInt(ResponseState.RESPONSE_STATUS_KEY);
                    switch(status){
                        case ResponseState.OK:
                            Toast.makeText(context,"登陆成功",Toast.LENGTH_SHORT).show();
                            JSONObject res = (JSONObject)response.get("user_info");
                            Intent toMainAcvitiy = new Intent(context,MainActivity.class);
                            BubbleDatingApplication.userEntity = new UserEntity(res.getInt(UserInfoKeys.U_ID),
                                    res.getString(UserInfoKeys.U_NAME),res.getString(UserInfoKeys.U_PASSWORD),
                                    res.getString(UserInfoKeys.U_EMAIL),res.getString(UserInfoKeys.U_GENDER),null,
                                    res.getBoolean(UserInfoKeys.U_ONLINE));
                            progressDialog.dismiss();
                            context.startActivity(toMainAcvitiy);
                            ((Activity)context).overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
                            ((Activity) context).finish();
                            break;
                        case ResponseState.UNKOWN_USERNAME:
                            progressDialog.dismiss();
                            Toast.makeText(context,"用户名错误",Toast.LENGTH_SHORT).show();
                            break;
                        case ResponseState.USERNAME_PASSWORD_UNCOMPATIBLE:
                            progressDialog.dismiss();
                            Toast.makeText(context,"密码与用户名不匹配",Toast.LENGTH_SHORT).show();
                            break;
                        default:
                            progressDialog.dismiss();
                            Toast.makeText(context,"未知的错误",Toast.LENGTH_SHORT).show();
                            break;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    Response.ErrorListener errorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError volleyError) {
            progressDialog.dismiss();
            Toast.makeText(LoginActivity.this, R.string.volley_request_timeout_error, Toast.LENGTH_LONG).show();
        }
    };


    public  void  login(final Context context){
        final String username = loginName.getText().toString();
        String pw = loginPw.getText().toString();
        if(TextUtils.isEmpty(username)){
            Toast.makeText(context,"用户名不能为空",Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(pw)){
            Toast.makeText(context,"密码不能为空",Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.show();
        Map<String,String> loginInfo = new HashMap<>();
        loginInfo.put("username",username);
        loginInfo.put("password",pw);
        loginInfo.put("lat",String.valueOf(BubbleDatingApplication.userLatLng.latitude));
        loginInfo.put("lon", String.valueOf(BubbleDatingApplication.userLatLng.longitude));
        Log.d(TAG, "-->USERNAME:" + username);
        Log.d(TAG,"-->pw:"+pw);


//        RequestQueue requestQueue = Volley.newRequestQueue(context);
        CustomRequest loginRequest = new CustomRequest(Request.Method.POST,LOGIN_URL,loginInfo,reponseListener, errorListener);
        RetryPolicy retryPolicy = new DefaultRetryPolicy(Configuration.REQUEST_TIMEOUT_MS,
                Configuration.MAX_RETRY_TIMES,Configuration.BACK_OFF_MULTI);
        loginRequest.setRetryPolicy(retryPolicy);
        RequestManager.getInstance(context).add(loginRequest);
    }

//    public static void  login(final Context context,String username,String pw){
//        Map<String,String> loginInfo = new HashMap<>();
//        loginInfo.put("username",username);
//        loginInfo.put("password",pw);
//        Log.d(TAG,"-->USERNAME:"+username);
//        Log.d(TAG,"-->pw:"+pw);
//
////        RequestQueue requestQueue = Volley.newRequestQueue(context);
//        CustomRequest loginRequest = new CustomRequest(Request.Method.POST,LOGIN_URL,loginInfo,response);
//        RequestManager.getInstance(context).add(loginRequest);
//    }
}
